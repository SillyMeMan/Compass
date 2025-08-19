package net.vinh.compass.network.packets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.vinh.compass.CompassLib;
import net.vinh.compass.effects.instance.custom.AuraVeilClientStorage;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.util.UUID;

public class AuraVeilS2CPacket {
	public static final Identifier ID = CompassLib.id("aura_veil_sync");

	public static void send(ServerPlayerEntity viewer, LivingEntity target, Formatting color, int duration) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(target.getId());
		buf.writeEnumConstant(color);
		buf.writeInt(duration);
		ServerPlayNetworking.send(viewer, ID, buf);
	}

	public static void registerClientReceiver() {
		ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) -> {
			int entityId = buf.readInt();
			Formatting color = buf.readEnumConstant(Formatting.class);
			int duration = buf.readInt();

			client.execute(() -> {
				if (client.world == null) return;
				Entity entity = client.world.getEntityById(entityId);
				if (entity instanceof LivingEntity living) {
					AuraVeilClientStorage.setAura(living.getUuid(), color, client.world.getTime() + duration);
				}
			});
		});
	}
}
