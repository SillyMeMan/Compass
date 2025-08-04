package net.vinh.compass.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

public class CompassUtil {
	private static final RandomGenerator random = RandomGenerator.getDefault();

	public static int getInt(int origin, int bound) {
		return random.nextInt(origin, bound);
	}

	public static List<Integer> getIntList(int amount, int origin, int bound) {
		List<Integer> list = new ArrayList<>();

		for (int i = amount; i > 0; i--) {
			list.add(getInt(origin, bound));
		}

		return list;
	}

	public static double getDouble(double origin, double bound) {
		return random.nextDouble(origin, bound);
	}

	public static List<Double> getDoubleList(int amount, double origin, double bound) {
		List<Double> list = new ArrayList<>();

		for (int i = amount; i > 0; i--) {
			list.add(getDouble(origin, bound));
		}

		return list;
	}

	public static float getFloat(float origin, float bound) {
		return random.nextFloat(origin, bound);
	}

	public static List<Float> getFloatList(int amount, float origin, float bound) {
		List<Float> list = new ArrayList<>();

		for (int i = amount; i > 0; i--) {
			list.add(getFloat(origin, bound));
		}

		return list;
	}

	public static long getLong(long origin, long bound) {
		return random.nextLong(origin, bound);
	}

	public static List<Long> getLongList(int amount, long origin, long bound) {
		List<Long> list = new ArrayList<>();

		for (int i = amount; i > 0; i--) {
			list.add(getLong(origin, bound));
		}

		return list;
	}

	public static List<Vec3d> getVec3dListFromBox(int amount, Box box) {
		List<Vec3d> list = new ArrayList<>();

		for (int i = amount; i > 0; i--) {
			list.add(new Vec3d(getDouble(box.minX, box.maxX), getDouble(box.minY, box.maxY), getDouble(box.minZ, box.maxZ)));
		}

		return list;
	}

	public static List<BlockPos> getBlockPosListFromBox(int amount, Box box) {
		List<BlockPos> list = new ArrayList<>();

		for (int i = amount; i > 0; i--) {
			list.add(new BlockPos(getDouble(box.minX, box.maxX), getDouble(box.minY, box.maxY), getDouble(box.minZ, box.maxZ)));
		}

		return list;
	}

	//Honestly, IDK why u would ever use this... but who cares?
	public static List<Position> getPositionListFromBox(int amount, Box box) {
		List<Position> list = new ArrayList<>();

		for (int i = amount; i > 0; i--) {
			list.add(new Position() {
				@Override
				public double getX() {
					return getDouble(box.minX, box.maxX);
				}

				@Override
				public double getY() {
					return getDouble(box.minY, box.maxY);
				}

				@Override
				public double getZ() {
					return getDouble(box.minZ, box.maxZ);
				}
			});
		}

		return list;
	}

	public static void sphericalExplosion(ServerWorld world, Vec3d center, float radius, double knockbackMultiplier, float maxDamage, PlayerEntity user, SoundEvent soundEvent, boolean emitParticle) {
		BlockPos centerPos = new BlockPos(MathHelper.floor(center.getX()), MathHelper.floor(center.getY()), MathHelper.floor(center.getZ()));
		int intRadius = MathHelper.ceil(radius);
		double radiusSq = radius * radius;

		Box box = new Box(
			center.x - radius, center.y - radius, center.z - radius,
			center.x + radius, center.y + radius, center.z + radius
		);

		List<Entity> entities = world.getOtherEntities(null, box);
		Set<Entity> processed = new HashSet<>();

		// Block destruction
		for (int x = -intRadius; x <= intRadius; x++) {
			for (int y = -intRadius; y <= intRadius; y++) {
				for (int z = -intRadius; z <= intRadius; z++) {
					BlockPos pos = centerPos.add(x, y, z);
					double distSq = centerPos.getSquaredDistance(pos);

					if (distSq <= radiusSq) {
						BlockState state = world.getBlockState(pos);
						if (!state.isAir() && state.getBlock().getBlastResistance() < 50.0F) {
							world.breakBlock(pos, true);
						}
					}
				}
			}
		}

		// Entity damage/knockback
		for (Entity entity : entities) {
			if (entity == user) continue;

			double dx = entity.getX() - center.x;
			double dy = entity.getY() - center.y;
			double dz = entity.getZ() - center.z;
			double distSq = dx * dx + dy * dy + dz * dz;

			if (distSq <= radiusSq && !processed.contains(entity)) {
				double dist = Math.sqrt(distSq);
				double exposure = 1.0 - (dist / radius);
				float damage = (float)(exposure * maxDamage);

				entity.damage(DamageSource.explosion(user), damage);

				Vec3d direction = entity.getPos().subtract(center).normalize();
				Vec3d knockback = new Vec3d(direction.x, direction.y + 1.5, direction.z).normalize()
					.multiply(exposure * knockbackMultiplier);

				entity.addVelocity(knockback.x, knockback.y, knockback.z);
				entity.velocityModified = true;

				processed.add(entity);
			}
		}

		if (soundEvent != null) {
			world.playSound(null, center.x, center.y, center.z, soundEvent, SoundCategory.MASTER, 10.0F, 1.0F);
		}

		if (emitParticle) {
			world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, center.x, center.y, center.z, 1, 0, 0, 0, 0);
		}
	}

	public static void damageWithCustomLogic(CompassDamageCalculationType damageCalculationType, DamageContext context) {
		damageCalculationType.applyWithCustomLogic(context);
	}

	public static void applyKnockbackAndEffects(DamageContext ctx, LivingEntity target) {
		if (!ctx.knockback.equals(Vec3d.ZERO)) {
			target.addVelocity(ctx.knockback.x, ctx.knockback.y, ctx.knockback.z);
			target.velocityModified = true;
		}

		for (StatusEffectInstance effect : ctx.statusEffects) {
			target.addStatusEffect(new StatusEffectInstance(effect));
		}
	}
}
