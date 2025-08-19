package net.vinh.compass.effects.instance.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuraVeilClientStorage {
	private static final Map<UUID, AuraData> activeAuras = new HashMap<>();

	public static void setAura(UUID target, Formatting color, long expiry) {
		activeAuras.put(target, new AuraData(color, expiry));
	}

	public static Formatting getAuraColor(LivingEntity target, long worldTime) {
		AuraData data = activeAuras.get(target.getUuid());
		if (data == null || worldTime > data.expiry) {
			activeAuras.remove(target.getUuid());
			return null;
		}
		return data.color;
	}

	private record AuraData(Formatting color, long expiry) {}
}
