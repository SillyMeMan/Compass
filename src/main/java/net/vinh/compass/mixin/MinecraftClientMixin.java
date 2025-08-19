package net.vinh.compass.mixin;

import net.minecraft.util.crash.CrashReport;
import net.vinh.compass.exception.CompassPunishmentException;
import net.vinh.compass.handlers.ScreenParticleHandler;
import net.vinh.compass.handlers.ScreenshakeHandler;
import net.vinh.compass.setup.CompassParticles;
import net.vinh.compass.setup.CompassScreenParticles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.vinh.compass.CompassLib.RANDOM;

@Mixin(MinecraftClient.class)
final class MinecraftClientMixin {
	@Inject(method = "run", at = @At("HEAD"), cancellable = true)
	private void compass$catchPunishment(CallbackInfo ci) {
		try {} catch (CompassPunishmentException pe) {
			CrashReport report = pe.getCrashReport();
			MinecraftClient client = (MinecraftClient) (Object) this;
			MinecraftClient.printCrashReport(report);
			ci.cancel();
		}
	}

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManager;registerReloader(Lnet/minecraft/resource/ResourceReloader;)V", ordinal = 17))
	private void compass$registerParticleFactories(RunArgs runArgs, CallbackInfo ci) {
		CompassParticles.registerFactories();
		CompassScreenParticles.registerParticleFactories();
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void compass$clientTick(CallbackInfo ci) {
		ScreenParticleHandler.clientTick();
		ScreenshakeHandler.clientTick(MinecraftClient.getInstance().gameRenderer.getCamera(), RANDOM);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 4, shift = At.Shift.AFTER))
	private void compass$renderTickThingamajig(boolean tick, CallbackInfo ci) {
		ScreenParticleHandler.renderParticles();
	}
}
