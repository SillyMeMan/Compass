package net.vinh.compass.util;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerScheduledExecutorService {

	private static final List<ScheduledTask> TASKS = new ArrayList<>();

	public static void tick(MinecraftServer server) {
		Iterator<ScheduledTask> iter = TASKS.iterator();
		while (iter.hasNext()) {
			ScheduledTask task = iter.next();
			if (--task.ticksLeft <= 0) {
				task.runnable.run();
				iter.remove();
			}
		}
	}

	public static void schedule(int delayTicks, Runnable runnable) {
		TASKS.add(new ScheduledTask(delayTicks, runnable));
	}

	private static class ScheduledTask {
		int ticksLeft;
		final Runnable runnable;

		ScheduledTask(int delayTicks, Runnable runnable) {
			this.ticksLeft = delayTicks;
			this.runnable = runnable;
		}
	}
}
