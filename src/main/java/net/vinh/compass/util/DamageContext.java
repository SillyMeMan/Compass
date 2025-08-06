package net.vinh.compass.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public interface DamageContext {
	World getWorld();
	DamageSource getDamageSource();
	List<LivingEntity> getTargets();
	Random getRandom();

	float getBaseDamage();
	int getTickInterval();
	int getDamageTicks();
	int getBounceTicks();

	Vec3d getKnockback();

	List<StatusEffectInstance> getStatusEffects();

	DamageContext setTargets(List<LivingEntity> targets);
	DamageContext setDamageSource(DamageSource source);

	DamageContext setKnockback(Vec3d knockback);

	DamageContext addStatusEffects(List<StatusEffectInstance> statusEffects);

	DamageContext setBaseDamage(float baseDamage);
	DamageContext setTickInterval(int tickInterval);
	DamageContext setDamageTicks(int damageTicks);
	DamageContext setBounceTicks(int bounceTicks);
}
