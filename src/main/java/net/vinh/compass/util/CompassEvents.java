package net.vinh.compass.util;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;
import net.vinh.compass.effects.CompassEffects;
import net.vinh.compass.effects.instance.custom.AuraVeilClientStorage;
import net.vinh.compass.network.packets.AuraVeilS2CPacket;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import org.quiltmc.qsl.lifecycle.api.event.ServerTickEvents;

import static net.vinh.compass.CompassLib.id;

public class CompassEvents {
	private static ShaderEffect unstableEyeEffect;
	private static int lastWidth = -1;
	private static int lastHeight = -1;

	public static void registerServerEvents() {
		ServerTickEvents.START.register(ServerScheduledExecutorService::tick);
		ServerTickEvents.END.register(ThisIsTheMostDangerousClassInCompass::tick);
		ServerTickEvents.END.register(ThisIsTheMostDangerousClassInCompass::tick);
		ServerTickEvents.END.register(server -> {
			if (!server.getOverworld().isClient()) {
				CompassUtil.ExplosionScheduler.tick(server.getOverworld());
			}
		});
	}

	public static void registerClientEvents() {
		AuraVeilS2CPacket.registerClientReceiver();

		HudRenderCallback.EVENT.register(ClientCastUIRenderer::render);

		ClientTickEvents.END.register(ThisIsTheMostDangerousClassInCompass.MissingTexturePrank::clientTick);

		ClientTickEvents.END.register(client -> {
			if (client.player == null) return;

			if (client.player.hasStatusEffect(CompassEffects.UNSTABLE_EYE)) {
				if (unstableEyeEffect == null) {
					try {
						unstableEyeEffect = new ShaderEffect(
							client.getTextureManager(),
							client.getResourceManager(),
							client.getFramebuffer(),
							id("shaders/post/impairment.json")
						);
						unstableEyeEffect.setupDimensions(client.getWindow().getFramebufferWidth(),
							client.getWindow().getFramebufferHeight());
					} catch (Exception e) {
						e.printStackTrace();
						unstableEyeEffect = null;
					}
				}
			} else {
				if (unstableEyeEffect != null) {
					unstableEyeEffect.close();
					unstableEyeEffect = null;
				}
			}
		});

		ClientTickEvents.END.register(client -> {
			if (client.world == null) return;
			long time = client.world.getTime();
			Scoreboard sb = client.world.getScoreboard();

			for (Entity entity : client.world.getEntities()) {
				if (!(entity instanceof LivingEntity living)) continue;

				Formatting color = AuraVeilClientStorage.getAuraColor(living, time);
				String uuidKey = living.getUuidAsString();
				String teamName = "aura_" + color;

				if (color != null) {
					// Ensure the team exists
					Team team = sb.getTeam(teamName);
					if (team == null) {
						team = sb.addTeam(teamName);
						team.setColor(color);
						team.setShowFriendlyInvisibles(true);
						team.setFriendlyFireAllowed(true);
					}

					// Check if entity is already in correct team
					Team currentTeam = sb.getPlayerTeam(uuidKey);
					if (currentTeam != team) {
						sb.addPlayerToTeam(uuidKey, team);
					}
				} else {
					// Clean up expired auras
					Team currentTeam = sb.getPlayerTeam(uuidKey);
					if (currentTeam != null && currentTeam.getName().startsWith("aura_")) {
						sb.removePlayerFromTeam(uuidKey, currentTeam);
					}
				}
			}
		});


		WorldRenderEvents.LAST.register(context -> {
			if (unstableEyeEffect != null) {
				int width = MinecraftClient.getInstance().getWindow().getFramebufferWidth();
				int height = MinecraftClient.getInstance().getWindow().getFramebufferHeight();

				if (width != lastWidth || height != lastHeight) {
					unstableEyeEffect.setupDimensions(width, height);
					lastWidth = width;
					lastHeight = height;
				}

				unstableEyeEffect.render(context.tickDelta());
			}
		});
	}
}
