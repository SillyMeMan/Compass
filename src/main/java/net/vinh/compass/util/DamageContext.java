package net.vinh.compass.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class DamageContext {
	public final List<LivingEntity> targets;
	public final float baseDamage;
	public final DamageSource source;
	public final World world;
	public final long currentTick;

	// Optional configs
	public int bounceTicks = 3;
	public int tickInterval = 10;

	public int damageTicks = 10;

	public Random random = new Random();

	public Vec3d knockback = Vec3d.ZERO;
	public List<StatusEffectInstance> statusEffects = List.of();

	public DamageContext(List<LivingEntity> targets, float baseDamage, DamageSource source, World world) {
		this.targets = targets;
		this.baseDamage = baseDamage;
		this.source = source;
		this.world = world;
		this.currentTick = world.getTime();
	}

	public DamageContext setBounceTicks(int ticks) {
		this.bounceTicks = ticks;
		return this;
	}

	public DamageContext setTickInterval(int interval) {
		this.tickInterval = interval;
		return this;
	}

	public DamageContext setDamageTicks(int damageTicks) {
		this.damageTicks = damageTicks;
		return this;
	}

	public DamageContext withKnockback(Vec3d knockback) {
		this.knockback = knockback;
		return this;
	}

	public DamageContext withStatusEffects(List<StatusEffectInstance> effects) {
		this.statusEffects = effects;
		return this;
	}

	public DamageContext setRandom(Random random) {
		this.random = random;
		return this;
	}
}
