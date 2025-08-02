package net.vinh.compass.util;

import net.minecraft.server.network.ServerPlayerEntity;

public interface AbilitySystem {
	void tick(ServerPlayerEntity player);

	void startCharging();

	void cancel();
		
	void applySuccessCooldown();

	void applyFailCooldown();

	boolean isCharging();

	boolean isOnCooldown();

	boolean canCast(ServerPlayerEntity player);

	void cast(ServerPlayerEntity player);
}
