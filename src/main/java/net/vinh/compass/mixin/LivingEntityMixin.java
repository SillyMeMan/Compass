package net.vinh.compass.mixin;

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
}
