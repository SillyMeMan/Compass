package net.vinh.compass.mixin;

import net.vinh.compass.handlers.PostProcessHandler;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/ShaderEffect;render(F)V", ordinal = 1))
	public void compass$injectionBeforeTransparencyChainProcess(CallbackInfo ci) {
		PostProcessHandler.copyDepthBuffer();
	}

}
