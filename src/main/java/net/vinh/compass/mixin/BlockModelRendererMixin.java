package net.vinh.compass.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.BlockRenderView;
import net.vinh.compass.util.ThisIsTheMostDangerousClassInCompass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockModelRenderer.class)
public abstract class BlockModelRendererMixin {
	@Inject(method = "render*", at = @At("HEAD"), cancellable = true)
	private void compass$useGlitchTextures(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertices, boolean cull, Random random, long seed, int overlay, CallbackInfoReturnable<Boolean> cir) {
		if (ThisIsTheMostDangerousClassInCompass.MissingTexturePrank.shouldGlitch(pos)) {
			if (!ThisIsTheMostDangerousClassInCompass.MissingTexturePrank.shouldGlitch(pos)) return;

			// Grab the purple/black checker sprite
			Sprite missing = MinecraftClient.getInstance()
				.getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
				.apply(MissingSprite.getMissingSpriteId());

			// Gather quads (all faces + null = non-directional)
			java.util.List<BakedQuad> quads = new java.util.ArrayList<>();
			for (Direction d : Direction.values()) quads.addAll(model.getQuads(state, d, (RandomGenerator) random));
			quads.addAll(model.getQuads(state, null, (RandomGenerator) random));

			// Light for this block
			int light = WorldRenderer.getLightmapCoordinates(world, state, pos);

			MatrixStack.Entry entry = matrices.peek();

			for (BakedQuad q : quads) {
				// Build a new quad with the missing sprite
				// NOTE: use whichever name your Yarn has: getColorIndex() or getTintIndex()
				int[] data = q.getVertexData().clone();
				int tint = (hasGetColorIndex(q) ? q.getColorIndex() : q.getColorIndex());
				BakedQuad missingQuad = new BakedQuad(data, tint, q.getFace(), missing, q.hasShade());

				// 7-arg helper: matrices, quad, r, g, b, light, overlay
				vertices.bakedQuad(entry, missingQuad, 1f, 1f, 1f, light, overlay);
			}

			// We rendered it ourselves; stop vanilla
			cir.setReturnValue(true);
		}
	}

	private static boolean hasGetColorIndex(BakedQuad q) {
		try { q.getClass().getMethod("getColorIndex"); return true; }
		catch (NoSuchMethodException e) { return false; }
	}
}
