package net.vinh.compass.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.vinh.compass.CompassLib;

import java.util.ArrayList;
import java.util.List;

import static net.vinh.compass.util.CompassUtil.getInt;

public enum CompassDamageCalculationTypes implements CompassDamageCalculationType{
	DISTRIBUTE((ctx -> {
		if (ctx.targets.isEmpty()) return;

		float finalDamage = ctx.baseDamage / ctx.targets.size() / ctx.damageTicks;

		for(LivingEntity target : ctx.targets) {
			for (int i = 0; i < ctx.damageTicks; i++) {
				int delay = i * ctx.tickInterval;

				ServerScheduledExecutorService.schedule(delay, () -> {
					target.damage(ctx.source, finalDamage);
					CompassUtil.applyKnockbackAndEffects(ctx, target);
				});
			}
		}
	})),
	AOE (ctx -> {
		float finalDamage = ctx.baseDamage / ctx.damageTicks;

		for (LivingEntity target : ctx.targets) {
			for (int i = 0; i < ctx.damageTicks; i++) {
				int delay = i * ctx.tickInterval;

				ServerScheduledExecutorService.schedule(delay, () -> {
					target.damage(ctx.source, finalDamage);
					CompassUtil.applyKnockbackAndEffects(ctx, target);
				});
			}
		}

	}),
	SINGLE_TARGET ((ctx -> {
		LivingEntity target;
		if(ctx.targets.size() > 1) {
			target = ctx.targets.get(ctx.random.nextInt(ctx.targets.size() - 1));
		}
		else {
			target = ctx.targets.get(0);
		}

		float finalDamage = ctx.baseDamage / ctx.damageTicks;

		for (int i = 0; i < ctx.damageTicks; i++) {
			int delay = i * ctx.tickInterval;

			ServerScheduledExecutorService.schedule(delay, () -> {
				target.damage(ctx.source, finalDamage);
				CompassUtil.applyKnockbackAndEffects(ctx, target);
			});
		}
	})),
	BOUNCE((ctx) -> {
		if (ctx.targets.isEmpty()) return;

		ctx.targets.removeIf(LivingEntity::isDead);
		if (ctx.targets.size() < 2) {
			AOE.applyWithCustomLogic(ctx.setDamageTicks(ctx.bounceTicks).setTickInterval(ctx.tickInterval));
			return;
		}

		float finalDamage = ctx.baseDamage / ctx.bounceTicks;

		for (int i = 0; i < ctx.bounceTicks; ) {
			// Filter alive targets again just in case
			List<LivingEntity> aliveTargets = ctx.targets.stream()
				.filter(LivingEntity::isAlive)
				.toList();

			if (aliveTargets.size() < 2) {
				CompassLib.LOGGER.warn("Insufficient valid targets for bounce. Falling back to AoE.");
				AOE.applyWithCustomLogic(ctx.setDamageTicks(ctx.bounceTicks - i).setTickInterval(ctx.tickInterval));
				return;
			}

			LivingEntity target = aliveTargets.get(ctx.random.nextInt(aliveTargets.size() - 1));
			int delay = i * ctx.tickInterval;

			if (ctx.world instanceof ServerWorld) {
				ServerScheduledExecutorService.schedule(delay, () -> {
					target.damage(ctx.source, finalDamage);
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
