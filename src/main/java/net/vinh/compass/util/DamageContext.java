package net.vinh.compass.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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

	DamageContext setTargets(List<LivingEntity> targets);
	DamageContext setDamageSource(DamageSource source);

	DamageContext setBaseDamage(float baseDamage);
	DamageContext setTickInterval(int tickInterval);
	DamageContext setDamageTicks(int damageTicks);
	DamageContext setBounceTicks(int bounceTicks);
}
