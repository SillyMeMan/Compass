package net.vinh.compass.setup;

import net.vinh.compass.helpers.DataHelper;
import net.vinh.compass.systems.rendering.particle.type.CompassParticleType;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.BiConsumer;

public class CompassParticles {
	public static final CompassParticleType WISP_PARTICLE = new CompassParticleType();
	public static final CompassParticleType SMOKE_PARTICLE = new CompassParticleType();
	public static final CompassParticleType SPARKLE_PARTICLE = new CompassParticleType();
	public static final CompassParticleType TWINKLE_PARTICLE = new CompassParticleType();
	public static final CompassParticleType STAR_PARTICLE = new CompassParticleType();

	public static void init() {
		initParticles(bind(Registry.PARTICLE_TYPE));
	}

	public static void registerFactories() {
		ParticleFactoryRegistry.getInstance().register(WISP_PARTICLE, CompassParticleType.Factory::new);
		ParticleFactoryRegistry.getInstance().register(SMOKE_PARTICLE, CompassParticleType.Factory::new);
		ParticleFactoryRegistry.getInstance().register(SPARKLE_PARTICLE, CompassParticleType.Factory::new);
		ParticleFactoryRegistry.getInstance().register(TWINKLE_PARTICLE, CompassParticleType.Factory::new);
		ParticleFactoryRegistry.getInstance().register(STAR_PARTICLE, CompassParticleType.Factory::new);
	}
	// shamelessly stolen from Botania
	private static void initParticles(BiConsumer<ParticleType<?>, Identifier> registry) {
		registry.accept(WISP_PARTICLE, DataHelper.prefix("wisp"));
		registry.accept(SMOKE_PARTICLE, DataHelper.prefix("smoke"));
		registry.accept(SPARKLE_PARTICLE, DataHelper.prefix("sparkle"));
		registry.accept(TWINKLE_PARTICLE, DataHelper.prefix("twinkle"));
		registry.accept(STAR_PARTICLE, DataHelper.prefix("star"));
	}
	// guess where this one comes from
	private static <T> BiConsumer<T, Identifier> bind(Registry<? super T> registry) {
		return (t, id) -> Registry.register(registry, id, t);
	}
}

