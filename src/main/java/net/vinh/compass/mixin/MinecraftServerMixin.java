package net.vinh.compass.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReport;
import net.vinh.compass.CompassLib;
import net.vinh.compass.exception.CompassPunishmentException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(method = "runServer", at = @At("HEAD"), cancellable = true)
	private void compass$catchPunishment(CallbackInfo ci) {
		try {} catch (CompassPunishmentException pe) {
			CrashReport report = pe.getCrashReport();

			File crashDir = new File("crash-reports");
			if (!crashDir.exists()) {
				crashDir.mkdirs();
			}

			File file = new File(crashDir,
				"crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt");

			try {
				FileWriter writer = new FileWriter(file);
				writer.write(report.asString());
				writer.close();
				CompassLib.LOGGER.error("This server crashed! Crash report saved to: {}", file.getAbsolutePath());
			} catch (IOException io) {
				CompassLib.LOGGER.error("Failed to save crash report", io);
			}

			((MinecraftServer)(Object)this).stop(false);

			ci.cancel();
		}
	}
}
