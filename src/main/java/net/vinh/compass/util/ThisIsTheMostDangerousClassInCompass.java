package net.vinh.compass.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.explosion.Explosion;
import net.vinh.compass.annotation.Dangerous;

import java.util.*;

import static net.vinh.compass.util.CompassUtil.getDouble;

/**
 * ☠️ WARNING ☠️
 * <p>
 * This class is intentionally chaotic and NOT for production use.
 * <p>
 * Functions in here may:
 * <p>
 * - Kick players
 * <p>
 * - Spam entities
 * <p>
 * - Lag out your game
 * <p>
 * - Spam teleport you
 * <p>
 * Importing this class means you accept the risk of pranking yourself
 * and/or your friends. DO NOT ship this in a public mod without
 * removing references to it. Here be dragons.
 */
@Dangerous("This class has dangerous capabilities and should be used under knowledge of the worst damage this class can do")
public class ThisIsTheMostDangerousClassInCompass {
	private static final Map<ServerPlayerEntity, TeleportSpamTask> teleportTasks = new HashMap<>();

	private static final int LIFETIME_TICKS = 20 * 5; // 10 seconds

	private static final List<Entity> spawned = new ArrayList<>();
	private static boolean needsClearing = false;

	private static final int[] ticksPassed = {0};

	/**
	 * CRASH
	 * <p>
	 * Crashes the player client
	 * @param client This client gets crashed
	 */
	public static void crash(MinecraftClient client) {
		CrashReport report = CrashReport.create(new Throwable("A mod triggered a crash on your client"), "Manually initiated crash");

		client.execute(() -> {
			throw new CrashException(report);
		});
	}

	/**
	 * BAN IP
	 * <p>
	 * (Self-explanatory)
	 * @param player This player and their IP gets banned
	 * @param reason Why this player and their IP got banned
	 */
	public static void ban_ip(ServerPlayerEntity player, String reason) {
		BannedIpList bannedIpList = Objects.requireNonNull(player.getServer()).getPlayerManager().getIpBanList();

		bannedIpList.add(new BannedIpEntry(player.getIp()));
		player.networkHandler.disconnect(Text.literal(reason));
	}

	/**
	 * BAN
	 * <p>
	 * (Self-explanatory)
	 * @param player This player gets banned
	 * @param reason Why this player got banned
	 */
	public static void ban(ServerPlayerEntity player, String reason) {
		BannedPlayerList bannedPlayerList = Objects.requireNonNull(player.getServer()).getPlayerManager().getUserBanList();

		bannedPlayerList.add(new BannedPlayerEntry(player.getGameProfile()));
		player.networkHandler.disconnect(Text.literal(reason));
	}

	/**
	 * THE ULTIMATE ENTITY BOMB
	 * <p>
	 * Spams a set number of the selected entity and removes them after 10 seconds (to prevent total server annihilation)
	 * @param world The world
	 * @param pos Entities will be spawn near this position
	 * @param entity The chosen entity
	 * @param amount The number of entities spawned
	 */
	@Dangerous("This method can completely crash a server if used with absurd parameters")
	public static void shortLivedEntityBomb(ServerWorld world, BlockPos pos, Entity entity, int amount) {
		needsClearing = true;

		for (int i = 0; i < amount; i++) {
			if (entity != null) {
				entity.refreshPositionAndAngles(
					pos.getX() + world.random.nextInt(10) - 5,
					pos.getY() + 2,
					pos.getZ() + world.random.nextInt(10) - 5,
					world.random.nextFloat() * 360.0F,
					0.0F
				);
				world.spawnEntity(entity);
				spawned.add(entity);
			}
		}
	}

	/**
	 * PORTAL ENTHUSIASTIC
	 * <p>
	 * Spam teleport the player every tick
	 * @param player The target player
	 * @param durationTicks How many ticks/teleport instances
	 * @param radius How far can the target teleport away from their current position
	 */
	public static void startSpamTeleport(ServerPlayerEntity player, int durationTicks, int radius) {
		teleportTasks.put(player, new TeleportSpamTask(player, durationTicks, radius));
	}

	/**
	 * THOR
	 * <p>
	 * Spams lightning
	 * @param world The world
	 * @param times	Amount of chaos
	 * @param range	Da range
	 */
	public static void thor(ServerWorld world, int times, Box range) {
		for (int i = 0; i < times; i++) {
			double x = getDouble(range.minX, range.maxX);
			double y = getDouble(range.minY, range.maxY);
			double z = getDouble(range.minZ, range.maxZ);

			LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
			if (lightning != null) {
				lightning.setPos(x, y, z);
				world.spawnEntity(lightning);
			}

			TntEntity tnt = new TntEntity(world, x, y, z, null);
			tnt.setFuse(200);
			world.spawnEntity(tnt);
		}
	}

	/**
	 * 💥 FIRE IN THE HOLE 💥
	 * <p>
	 * Launches the player into the air, spawns fireworks,
	 * plays a big explosion sound, and drops primed TNT.
	 *
	 * @param player Target player
	 */
	public static void fireInTheHole(ServerPlayerEntity player) {
		ServerWorld world = player.getWorld();

		player.addVelocity(0, 2, 0);
		player.velocityModified = true;

		world.spawnParticles(
			ParticleTypes.FIREWORK,
			player.getX(), player.getY() + 1, player.getZ(),
			50, 1.0, 1.0, 1.0, 0.2
		);

		world.playSound(null,
			player.getBlockPos(),
			SoundEvents.ENTITY_GENERIC_EXPLODE,
			SoundCategory.PLAYERS,
			2.0F, 0.8F
		);

		TntEntity tnt = new TntEntity(world,
			player.getX(), player.getY(), player.getZ(), player);
		tnt.setFuse(40);

		world.spawnEntity(tnt);
	}

	public static void tick(MinecraftServer server) {
		if (needsClearing) {
			ticksPassed[0]++;

			if (ticksPassed[0] >= LIFETIME_TICKS) {
				for (Entity entity : spawned) {
					entity.getWorld().createExplosion(entity, entity.getX(), entity.getY(), entity.getZ(), 10f, true, Explosion.DestructionType.NONE);
					if (entity.isAlive()) {
						entity.discard();
					}
				}
				needsClearing = false;
			}
		}

		Iterator<Map.Entry<ServerPlayerEntity, TeleportSpamTask>> it = teleportTasks.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<ServerPlayerEntity, TeleportSpamTask> entry = it.next();
			TeleportSpamTask task = entry.getValue();
			if (!task.tick(server)) {
				it.remove();
			}
		}
	}

	private static class TeleportSpamTask {
		private final ServerPlayerEntity player;
		private final int radius;
		private int ticksRemaining;

		TeleportSpamTask(ServerPlayerEntity player, int durationTicks, int radius) {
			this.player = player;
			this.radius = radius;
			this.ticksRemaining = durationTicks;
		}

		boolean tick(MinecraftServer server) {
			ticksRemaining--;
			if (ticksRemaining <= 0) {
				ServerPlayerEntity player = getPlayer(server);
				if (player != null) {
					ThisIsTheMostDangerousClassInCompass.fireInTheHole(player);
				}
				return false;
			}

			ServerPlayerEntity player = getPlayer(server);
			if (player == null) return false;

			ServerWorld world = player.getWorld();
			BlockPos center = player.getBlockPos();

			double x = center.getX() + world.random.nextInt(radius * 2) - radius;
			double y = center.getY() + world.random.nextInt(10) - 5;
			double z = center.getZ() + world.random.nextInt(radius * 2) - radius;

			player.requestTeleport(x, y, z);

			world.spawnParticles(net.minecraft.particle.ParticleTypes.PORTAL,
				x, y + 1, z,
				20, 0.5, 0.5, 0.5, 0.1);
			world.playSound(null, x, y, z,
				net.minecraft.sound.SoundEvents.ENTITY_ENDERMAN_TELEPORT,
				net.minecraft.sound.SoundCategory.PLAYERS,
				1.0F, 1.0F);

			return true;
		}

		private ServerPlayerEntity getPlayer(MinecraftServer server) {
			for (ServerPlayerEntity player : server.getOverworld().getPlayers()) {
				if(player.equals(this.player)) {
					return player;
				}
			}
			return null;
		}
	}

	public static class MissingTexturePrank {
		private static boolean active;
		private static int ticksLeft;
		private static double chance = 0.3; // 30%
		private static final java.util.Random R = new java.util.Random();
		private static final java.util.Set<BlockPos> corrupted = new java.util.HashSet<>();

		public static void start(int durationTicks, double probability) {
			active = true;
			ticksLeft = durationTicks;
			chance = probability;
			corrupted.clear();
		}

		public static void clientTick(MinecraftClient client) {
			if (!active) return;
			if (--ticksLeft <= 0) {
				active = false;
				corrupted.clear();
				return;
			}
			if ((ticksLeft & 1) == 0) corrupted.clear(); // every 2 ticks (~0.1s)
		}

		public static boolean shouldGlitch(BlockPos pos) {
			if (!active) return false;
			// de-dup immutable pos
			BlockPos key = pos.toImmutable();
			if (!corrupted.contains(key) && R.nextDouble() < chance) corrupted.add(key);
			return corrupted.contains(key);
		}
	}
}
