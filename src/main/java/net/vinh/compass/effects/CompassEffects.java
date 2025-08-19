package net.vinh.compass.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.registry.Registry;
import net.vinh.compass.CompassLib;
import net.vinh.compass.effects.custom.AuraVeilEffect;
import net.vinh.compass.effects.custom.UnstableEyeEffect;

public class CompassEffects {
	public static final StatusEffect UNSTABLE_EYE = Registry.register(Registry.STATUS_EFFECT, CompassLib.id("unstable_eye"), new UnstableEyeEffect());
	public static final StatusEffect AURA_VEIL = Registry.register(Registry.STATUS_EFFECT, CompassLib.id("aura_veil"), new AuraVeilEffect());

	public static void register() {

	}
}
