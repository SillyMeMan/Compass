package net.vinh.compass;

import net.vinh.compass.helpers.OrtTestItem;
import net.vinh.compass.setup.CompassParticles;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.registry.Registry;
import net.vinh.compass.util.ServerScheduledExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents;

public class CompassLib implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("CompassLib");
	public static final String MODID= "compass";
	public static final RandomGenerator RANDOM = RandomGenerator.createLegacy();

	@Override
	public void onInitialize(ModContainer mod) {
		CompassParticles.init();
		if(QuiltLoader.isDevelopmentEnvironment()) {
			Registry.register(Registry.ITEM, id("ort"), new OrtTestItem(new QuiltItemSettings().rarity(Rarity.EPIC).group(ItemGroup.MISC)));
		}

		ServerTickEvents.START.register(ServerScheduledExecutorService::tick);
	}
	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
}
