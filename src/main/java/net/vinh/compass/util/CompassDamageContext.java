package net.vinh.compass.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class CompassDamageContext implements DamageContext{
	protected final World world;
	protected DamageSource source;
	protected List<LivingEntity> targets;
	protected final Random random;

	protected float baseDamage = 1.0f;
	protected int tickInterval = 1;
	protected int damageTicks = 1;
	protected int bounceTicks = 1;

	public CompassDamageContext(World world) {
		this.random = new Random();
		this.world = world;
	}

	public CompassDamageContext setBounceTicks(int ticks) {
		this.bounceTicks = ticks;
		return this;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public DamageSource getDamageSource() {
		return source;
	}

	@Override
	public List<LivingEntity> getTargets() {
		return targets;
	}

	@Override
	public Random getRandom() {
		return random;
	}

	@Override
	public float getBaseDamage() {
		return baseDamage;
	}

	@Override
	public int getTickInterval() {
		return tickInterval;
	}

	@Override
	public int getDamageTicks() {
		return damageTicks;
	}

	@Override
	public int getBounceTicks() {
		return bounceTicks;
	}

	@Override
	public DamageContext setTargets(List<LivingEntity> targets) {
		this.targets = targets;
		return this;
	}

	@Override
	public DamageContext setDamageSource(DamageSource source) {
		this.source = source;
		return this;
	}

	@Override
	public DamageContext setBaseDamage(float baseDamage) {
		this.baseDamage = baseDamage;
		return this;
	}

	@Override
	public DamageContext setTickInterval(int tickInterval) {
		this.tickInterval = tickInterval;
		return this;
	}

	@Override
	public DamageContext setDamageTicks(int damageTicks) {
		this.damageTicks = damageTicks;
		return this;
	}
}
