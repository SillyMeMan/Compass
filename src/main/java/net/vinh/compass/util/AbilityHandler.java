package net.vinh.compass.util;

import net.minecraft.server.network.ServerPlayerEntity;

public interface AbilityHandler {
	void tick(ServerPlayerEntity player);

	void startCharging(ServerPlayerEntity player);

	void cancel(ServerPlayerEntity player);

	void applySuccessCooldown(ServerPlayerEntity player);

	void applyFailCooldown(ServerPlayerEntity player);

	boolean isCharging(ServerPlayerEntity player);

	boolean isOnCooldown(ServerPlayerEntity player);

	boolean canCast(ServerPlayerEntity player);

	void cast(ServerPlayerEntity player);
}
