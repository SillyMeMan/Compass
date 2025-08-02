package net.vinh.compass;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.vinh.compass.config.ClientConfig;
import net.vinh.compass.handlers.RenderHandler;
import net.vinh.compass.network.screenshake.PositionedScreenshakePacket;
import net.vinh.compass.network.screenshake.ScreenshakePacket;
import net.vinh.compass.setup.CompassRenderLayers;
import eu.midnightdust.lib.config.MidnightConfig;
import net.vinh.compass.util.ClientCastUIRenderer;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import static net.vinh.compass.CompassLib.MODID;

public class CompassLibClient implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		MidnightConfig.init(MODID, ClientConfig.class);

		CompassRenderLayers.yea();
		RenderHandler.init();
//		OrtusParticles.init();

		HudRenderCallback.EVENT.register(ClientCastUIRenderer::render);

		ClientPlayNetworking.registerGlobalReceiver(ScreenshakePacket.ID, (client, handler, buf, responseSender) -> new ScreenshakePacket(buf).apply(client.getNetworkHandler()));
		ClientPlayNetworking.registerGlobalReceiver(PositionedScreenshakePacket.ID, (client, handler, buf, responseSender) -> PositionedScreenshakePacket.fromBuf(buf).apply(client.getNetworkHandler()));
	}
}
