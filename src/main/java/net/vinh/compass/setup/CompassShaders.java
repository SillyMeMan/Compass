package net.vinh.compass.setup;

import com.mojang.blaze3d.vertex.VertexFormats;
import com.mojang.datafixers.util.Pair;
import net.vinh.compass.CompassLib;
import net.vinh.compass.systems.rendering.ExtendedShader;
import net.vinh.compass.systems.rendering.ShaderHolder;
import net.minecraft.client.render.ShaderProgram;
import net.minecraft.resource.ResourceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CompassShaders {
	public static List<Pair<ShaderProgram, Consumer<ShaderProgram>>> shaderList;
	public static ShaderHolder ADDITIVE_TEXTURE = new ShaderHolder();
	public static ShaderHolder COMPASS_PARTICLE = new ShaderHolder();
	public static ShaderHolder ADDITIVE_PARTICLE = new ShaderHolder();

	public static ShaderHolder MASKED_TEXTURE = new ShaderHolder();
	public static ShaderHolder DISTORTED_TEXTURE = new ShaderHolder("Speed", "TimeOffset", "Intensity", "XFrequency", "YFrequency", "UVCoordinates");
	public static ShaderHolder METALLIC_NOISE = new ShaderHolder("Intensity", "Size", "Speed", "Brightness");
	public static ShaderHolder RADIAL_NOISE = new ShaderHolder("Speed", "XFrequency", "YFrequency", "Intensity", "ScatterPower", "ScatterFrequency", "DistanceFalloff");
	public static ShaderHolder RADIAL_SCATTER_NOISE = new ShaderHolder("Speed", "XFrequency", "YFrequency", "Intensity", "ScatterPower", "ScatterFrequency", "DistanceFalloff");

	public static ShaderHolder VERTEX_DISTORTION = new ShaderHolder();
	//public static ShaderHolder BLOOM = new ShaderHolder();

	public static ShaderHolder SCROLLING_TEXTURE = new ShaderHolder("Speed");
	public static ShaderHolder TRIANGLE_TEXTURE = new ShaderHolder();
	public static ShaderHolder COLOR_GRADIENT_TEXTURE = new ShaderHolder("DarkColor");
	public static ShaderHolder SCROLLING_TRIANGLE_TEXTURE = new ShaderHolder("Speed");

	public static void init(ResourceManager manager) throws IOException {
		shaderList = new ArrayList<>();
		registerShader(ExtendedShader.createShaderInstance(COMPASS_PARTICLE, manager, CompassLib.id("particle"), VertexFormats.POSITION_TEXTURE_COLOR_LIGHT));
		registerShader(ExtendedShader.createShaderInstance(ADDITIVE_TEXTURE, manager, CompassLib.id("additive_texture"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT));
		registerShader(ExtendedShader.createShaderInstance(ADDITIVE_PARTICLE, manager, CompassLib.id("additive_particle"), VertexFormats.POSITION_TEXTURE_COLOR_LIGHT));

		registerShader(ExtendedShader.createShaderInstance(DISTORTED_TEXTURE, manager, CompassLib.id("noise/distorted_texture"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT));
		registerShader(ExtendedShader.createShaderInstance(METALLIC_NOISE, manager, CompassLib.id("noise/metallic"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT));
		registerShader(ExtendedShader.createShaderInstance(RADIAL_NOISE, manager, CompassLib.id("noise/radial_noise"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT));
		registerShader(ExtendedShader.createShaderInstance(RADIAL_SCATTER_NOISE, manager, CompassLib.id("noise/radial_scatter_noise"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT));

		registerShader(ExtendedShader.createShaderInstance(SCROLLING_TEXTURE, manager, CompassLib.id("vfx/scrolling_texture"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT));
		registerShader(ExtendedShader.createShaderInstance(TRIANGLE_TEXTURE, manager, CompassLib.id("vfx/triangle_texture"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT));
		registerShader(ExtendedShader.createShaderInstance(SCROLLING_TRIANGLE_TEXTURE, manager, CompassLib.id("vfx/scrolling_triangle_texture"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT));
	}
	public static void registerShader(ExtendedShader extendedShaderInstance) {
		registerShader(extendedShaderInstance, (shader) -> ((ExtendedShader) shader).getHolder().setInstance((ExtendedShader) shader));
	}
	public static void registerShader(ShaderProgram shader, Consumer<ShaderProgram> onLoaded)
	{
		shaderList.add(Pair.of(shader, onLoaded));
	}
}
