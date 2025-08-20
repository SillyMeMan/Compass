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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockModelRenderer.class)
public abstract class BlockModelRendererMixin {
	@Inject(method = "render(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/util/math/MatrixStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/random/RandomGenerator;JI)V", at = @At("HEAD"))
	private void compass$useGlitchTextures(BlockRenderView world, BakedModel model, BlockState state, BlockPos pos, MatrixStack matrix, VertexConsumer vertexConsumer, boolean cull, RandomGenerator random, long seed, int overlay, CallbackInfo ci) {
		if (ThisIsTheMostDangerousClassInCompass.MissingTexturePrank.shouldGlitch(pos)) {
			if (!ThisIsTheMostDangerousClassInCompass.MissingTexturePrank.shouldGlitch(pos)) return;

			Sprite missing = MinecraftClient.getInstance()
				.getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
				.apply(MissingSprite.getMissingSpriteId());

			java.util.List<BakedQuad> quads = new java.util.ArrayList<>();
			for (Direction d : Direction.values()) quads.addAll(model.getQuads(state, d, random));
			quads.addAll(model.getQuads(state, null, random));

			// Light for this block
			int light = WorldRenderer.getLightmapCoordinates(world, state, pos);

			MatrixStack.Entry entry = matrix.peek();

			for (BakedQuad q : quads) {
				int[] data = q.getVertexData().clone();
				int tint = (q.getColorIndex());
				BakedQuad missingQuad = new BakedQuad(data, tint, q.getFace(), missing, q.hasShade());

				vertexConsumer.bakedQuad(entry, missingQuad, 1f, 1f, 1f, light, overlay);
			}
		}
	}
}
