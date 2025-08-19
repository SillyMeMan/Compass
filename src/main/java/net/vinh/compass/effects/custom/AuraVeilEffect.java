package net.vinh.compass.effects.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.vinh.compass.network.packets.AuraVeilS2CPacket;

import java.util.List;

public class AuraVeilEffect extends StatusEffect {
	public AuraVeilEffect() {
		super(StatusEffectType.HARMFUL, 0x9e0519);
	}

	public static void applyAura(LivingEntity target, Formatting color, int duration, List<ServerPlayerEntity> viewers) {
		for (ServerPlayerEntity viewer : viewers) {
			AuraVeilS2CPacket.send(viewer, target, color, duration);
		}
	}
}
