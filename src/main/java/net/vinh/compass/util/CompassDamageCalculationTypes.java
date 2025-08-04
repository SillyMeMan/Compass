package net.vinh.compass.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

import static net.vinh.compass.util.CompassUtil.getInt;

public enum CompassDamageCalculationTypes implements CompassDamageCalculationType{
	DISTRIBUTE((ctx -> {
		if (ctx.targets.isEmpty()) return;

		float finalDamage = ctx.baseDamage / ctx.targets.size();

		for(LivingEntity entity : ctx.targets) {
			entity.damage(ctx.source, finalDamage);
			CompassUtil.applyKnockbackAndEffects(ctx, entity);
		}
	})),
	AOE (ctx -> {
		for (LivingEntity entity : ctx.targets) {
			entity.damage(ctx.source, ctx.baseDamage);
			CompassUtil.applyKnockbackAndEffects(ctx, entity);
		}
	}),
	SINGLE_TARGET ((ctx -> {
		LivingEntity entity = ctx.targets.get(getInt(0, ctx.targets.size() - 1));

		entity.damage(ctx.source, ctx.baseDamage);
		CompassUtil.applyKnockbackAndEffects(ctx, entity);
	})),
	BOUNCE ((ctx -> {
		if(ctx.targets.isEmpty()) return;

		float finalDamage = ctx.baseDamage / ctx.bounceTicks;

		for (int i = 0; i < ctx.bounceTicks; i++) {
			int delay = i * ctx.tickInterval;
			LivingEntity target = ctx.targets.get(ctx.random.nextInt(ctx.targets.size() - 1));

			if (ctx.world instanceof ServerWorld serverWorld) {
				ServerScheduledExecutorService.schedule(ctx.tickInterval, () -> target.damage(ctx.source, finalDamage));
				CompassUtil.applyKnockbackAndEffects(ctx, target);
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
