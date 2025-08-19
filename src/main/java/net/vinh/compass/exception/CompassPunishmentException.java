package net.vinh.compass.exception;

import net.minecraft.util.crash.CrashReport;

public class CompassPunishmentException extends RuntimeException {
	private final CrashReport crashReport;

	public CompassPunishmentException(CrashReport crashReport) {
		super(crashReport.getMessage()); // optional: use the report’s message
		this.crashReport = crashReport;
	}

	public CompassPunishmentException(String message, CrashReport crashReport) {
		super(message);
		this.crashReport = crashReport;
	}

	public CrashReport getCrashReport() {
		return crashReport;
	}
}
