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
		LivingEntity target = ctx.targets.get(getInt(0, ctx.targets.size() - 1));

		float finalDamage = ctx.baseDamage / ctx.damageTicks;

		for (int i = 0; i < ctx.damageTicks; i++) {
			int delay = i * ctx.tickInterval;

			ServerScheduledExecutorService.schedule(delay, () -> {
				target.damage(ctx.source, finalDamage);
				CompassUtil.applyKnockbackAndEffects(ctx, target);
			});
		}
	})),
	BOUNCE ((ctx -> {
		if(ctx.targets.isEmpty()) return;

		float finalDamage = ctx.baseDamage / ctx.bounceTicks;

		for (int i = 0; i < ctx.bounceTicks;) {
			List<Boolean> isAllDead = new ArrayList<>(List.of());
			ctx.targets.forEach(entity -> {
				isAllDead.add(entity.isDead());
			});
			if(isAllDead.contains(false)) {
				int delay = i * ctx.tickInterval;

				LivingEntity target = ctx.targets.get(ctx.random.nextInt(ctx.targets.size() - 1));
				if(target.isAlive()) {
					if(ctx.world instanceof ServerWorld) {
						ServerScheduledExecutorService.schedule(delay, () -> {
							target.damage(ctx.source, finalDamage);
							CompassUtil.applyKnockbackAndEffects(ctx, target);
						});
						i++;
					}
				}
				else {
					CompassLib.LOGGER.warn("Entity {} doesn't exist", target);
				}
			}
			else {
				CompassLib.LOGGER.warn("All entities provided are already dead. Suppressing potential infinite loop");
				return;
			}
		}
	}));

	private final CompassDamageCalculationType damageCalculationTypeLogic;

	CompassDamageCalculationTypes(CompassDamageCalculationType damageCalculationTypeLogic) {
		this.damageCalculationTypeLogic = damageCalculationTypeLogic;
	}

	@Override
	public void applyWithCustomLogic(DamageContext ctx) {
		damageCalculationTypeLogic.applyWithCustomLogic(ctx);
	}
}
