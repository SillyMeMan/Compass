package net.vinh.compass.effects.instance.custom;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.UUID;

public class AuraVeilStatusEffectInstance extends StatusEffectInstance {
	private final Formatting auraColor;
	private final List<UUID> viewers; // store as UUIDs for network safety

	public AuraVeilStatusEffectInstance(StatusEffect type, int duration, int amplifier, Formatting auraColor, List<ServerPlayerEntity> viewers) {
		super(type, duration, amplifier, false, false, true);
		this.auraColor = auraColor;
		this.viewers = viewers.stream().map(ServerPlayerEntity::getUuid).toList();
	}

	public Formatting getAuraColor() {
		return auraColor;
	}

	public List<UUID> getViewers() {
		return viewers;
	}
}
