package net.vinh.compass.mixin;

import net.vinh.compass.config.ClientConfig;
import net.vinh.compass.handlers.ScreenshakeHandler;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.vinh.compass.CompassLib.RANDOM;


@Mixin(Camera.class)
public class CameraMixin {
	@Inject(method = "update", at = @At("RETURN"))
	private void compassScreenshake(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
		if (ClientConfig.SCREENSHAKE_INTENSITY > 0) {
			ScreenshakeHandler.cameraTick((Camera) (Object) this, RANDOM);
		}
	}
}
