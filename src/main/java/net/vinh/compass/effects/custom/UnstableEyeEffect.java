package net.vinh.compass.effects.custom;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;

public class UnstableEyeEffect extends StatusEffect {
	public UnstableEyeEffect() {
		super(StatusEffectType.BENEFICIAL, 0x16de16);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}
}
