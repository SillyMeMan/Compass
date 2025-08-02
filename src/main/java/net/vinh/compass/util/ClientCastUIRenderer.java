package net.vinh.compass.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ClientCastUIRenderer {
	private static final MinecraftClient client = MinecraftClient.getInstance();

	private static final int MESSAGE_DURATION_TICKS = 60; // 3 seconds at 20 TPS

	private static boolean isCharging = false;
	private static int resultTicks = 0;
	private static Text displayText = Text.literal("");
	private static Mode currentMode = Mode.NONE;

	private enum Mode {
		NONE, CHARGING, SUCCESS, INTERRUPT
	}

	public static void startCharging(Text chargeText) {
		isCharging = true;
		currentMode = Mode.CHARGING;
		displayText = chargeText;
		resultTicks = 0;
	}

	public static void cancelCharging(Text cancelText) {
		isCharging = false;
		currentMode = Mode.INTERRUPT;
		displayText = cancelText;
		resultTicks = MESSAGE_DURATION_TICKS;
	}

	public static void finishCharging(Text successText) {
		isCharging = false;
		currentMode = Mode.SUCCESS;
		displayText = successText;
		resultTicks = MESSAGE_DURATION_TICKS;
	}

	public static void render(MatrixStack matrices, float tickDelta) {
		if (client.player == null || client.options.hudHidden) return;
		if (currentMode == Mode.NONE) return;

		if (currentMode != Mode.CHARGING) {
			if (resultTicks-- <= 0) {
				currentMode = Mode.NONE;
				return;
			}
		}

		TextRenderer textRenderer = client.textRenderer;
		int screenWidth = client.getWindow().getScaledWidth();
		int screenHeight = client.getWindow().getScaledHeight();

		int textWidth = textRenderer.getWidth(displayText);
		int x = (screenWidth - textWidth) / 2;
		int y = screenHeight / 2 + 50;

		int color = switch (currentMode) {
			case CHARGING -> 0xFFFFAA00; // orange
			case SUCCESS -> 0xFF55FF55;  // green
			case INTERRUPT -> 0xFFFF5555; // red
			default -> 0xFFFFFFFF;
		};

		textRenderer.drawWithShadow(matrices, displayText, x, y, color);
	}
}
