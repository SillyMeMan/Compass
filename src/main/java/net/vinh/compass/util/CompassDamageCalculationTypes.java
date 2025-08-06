package net.vinh.compass.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.vinh.compass.CompassLib;

import java.util.List;

public enum CompassDamageCalculationTypes implements CompassDamageCalculationType{
	DISTRIBUTE((ctx -> {
		if (ctx.getTargets().isEmpty()) return;

		float finalDamage = ctx.getBaseDamage() / ctx.getTargets().size() / ctx.getDamageTicks();

		for(LivingEntity target : ctx.getTargets()) {
			for (int i = 0; i < ctx.getDamageTicks(); i++) {
				int delay = i * ctx.getTickInterval();

				ServerScheduledExecutorService.schedule(delay, () -> {
					target.damage(ctx.getDamageSource(), finalDamage);
					CompassUtil.applyKnockbackAndEffects(ctx, target);
				});
			}
		}
	})),
	AOE (ctx -> {
		float finalDamage = ctx.getBaseDamage() / ctx.getDamageTicks();

		for (LivingEntity target : ctx.getTargets()) {
			for (int i = 0; i < ctx.getDamageTicks(); i++) {
				int delay = i * ctx.getTickInterval();

				ServerScheduledExecutorService.schedule(delay, () -> {
					target.damage(ctx.getDamageSource(), finalDamage);
					CompassUtil.applyKnockbackAndEffects(ctx, target);
				});
			}
		}

	}),
	SINGLE_TARGET ((ctx -> {
		LivingEntity target;
		if(ctx.getTargets().size() > 1) {
			target = ctx.getTargets().get(ctx.getRandom().nextInt(ctx.getTargets().size() - 1));
		}
		else {
			target = ctx.getTargets().get(0);
		}

		float finalDamage = ctx.getBaseDamage() / ctx.getBaseDamage();

		for (int i = 0; i < ctx.getDamageTicks(); i++) {
			int delay = i * ctx.getTickInterval();

			ServerScheduledExecutorService.schedule(delay, () -> {
				target.damage(ctx.getDamageSource(), finalDamage);
				CompassUtil.applyKnockbackAndEffects(ctx, target);
			});
		}
	})),
	BOUNCE((ctx) -> {
		if (ctx.getTargets().isEmpty()) return;

		ctx.getTargets().removeIf(LivingEntity::isDead);
		if (ctx.getTargets().size() < 2) {
			AOE.applyWithCustomLogic(ctx.setDamageTicks(ctx.getBounceTicks()).setTickInterval(ctx.getTickInterval()));
			return;
		}

		float finalDamage = ctx.getBaseDamage() / ctx.getBounceTicks();

		for (int i = 0; i < ctx.getBounceTicks(); ) {
			// Filter alive targets again just in case
			List<LivingEntity> aliveTargets = ctx.getTargets().stream()
				.filter(LivingEntity::isAlive)
				.toList();

			if (aliveTargets.size() < 2) {
				CompassLib.LOGGER.warn("Insufficient valid targets for bounce. Falling back to AoE.");
				AOE.applyWithCustomLogic(ctx.setDamageTicks(ctx.getBounceTicks() - i).setTickInterval(ctx.getTickInterval()));
				return;
			}

			LivingEntity target = aliveTargets.get(ctx.getRandom().nextInt(aliveTargets.size() - 1));
			int delay = i * ctx.getTickInterval();

			if (ctx.getWorld() instanceof ServerWorld) {
				ServerScheduledExecutorService.schedule(delay, () -> {
					target.damage(ctx.getDamageSource(), finalDamage);
					CompassUtil.applyKnockbackAndEffects(ctx, target);
				});
			}
			i++;
		}
	});

	private final CompassDamageCalculationType damageCalculationTypeLogic;

	CompassDamageCalculationTypes(CompassDamageCalculationType damageCalculationTypeLogic) {
		this.damageCalculationTypeLogic = damageCalculationTypeLogic;
	}

	@Override
	public void applyWithCustomLogic(DamageContext ctx) {
		damageCalculationTypeLogic.applyWithCustomLogic(ctx);
	}
}
