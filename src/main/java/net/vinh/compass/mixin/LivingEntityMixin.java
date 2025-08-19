package net.vinh.compass.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.vinh.compass.effects.CompassEffects;
import net.vinh.compass.event.ServerLivingEntityEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	// ALLOW_DEATH — runs before the entity dies, can cancel
	@Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
	private void beforeEntityKilled(DamageSource source, CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (!ServerLivingEntityEvents.ALLOW_DEATH.invoker().allowDeath(entity, source, 0f)) {
			ci.cancel();
		}
	}

	// AFTER_DEATH — runs after the entity dies
	@Inject(method = "onDeath", at = @At("TAIL"))
	private void notifyDeath(DamageSource source, CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		ServerLivingEntityEvents.AFTER_DEATH.invoker().afterDeath(entity, source);
	}

	// ALLOW_DAMAGE — runs before damage is applied
	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	private void beforeDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (!ServerLivingEntityEvents.ALLOW_DAMAGE.invoker().allowDamage(entity, source, amount)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
	private void compass$unstableEye(CallbackInfoReturnable<Boolean> cir) {
		MinecraftClient client = MinecraftClient.getInstance();
		PlayerEntity local = client.player;
		LivingEntity self = (LivingEntity)(Object)this;

		if (local != null && local.hasStatusEffect(CompassEffects.AURA_VEIL) && self != local && self instanceof PlayerEntity) {
			cir.setReturnValue(true);
		}
	}
}
