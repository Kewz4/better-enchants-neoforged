package net.da0ne.betterenchants;

import net.fabricmc.api.ModInitializer;

import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SequencedMap;
import java.util.function.Function;

public class BetterEnchants implements ModInitializer {
	public static final String MOD_ID = "better-enchants";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final RenderLayer cutoutLayer = RenderLayer.of(
			"custom_enchants_cutout",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			786432,
			true,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.program(RenderLayer.CUTOUT_PROGRAM)
					.writeMaskState(RenderLayer.DEPTH_MASK)
					.texture(RenderLayer.BLOCK_ATLAS_TEXTURE)
					.build(true)
	);

	private static RenderLayer createArmorRenderLayer(Identifier texture){
		return RenderLayer.of(
			"custom_enchants_armor",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			1536,
			true,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.program(RenderLayer.ARMOR_CUTOUT_NO_CULL_PROGRAM)
					.texture(new RenderPhase.Texture(texture, TriState.FALSE, false))
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.layering(RenderLayer.VIEW_OFFSET_Z_LAYERING)
					.depthTest(RenderLayer.LEQUAL_DEPTH_TEST)
					.writeMaskState(RenderLayer.DEPTH_MASK)
					.build(true));
	}

	public static final RenderLayer solidLayer = RenderLayer.of(
			"custom_enchants_cutout",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			786432,
			true,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.program(RenderLayer.SOLID_PROGRAM)
					.writeMaskState(RenderLayer.DEPTH_MASK)
					//.texture(RenderLayer.BLOCK_ATLAS_TEXTURE)
					.build(true)
	);

	public static final CustomRenderLayers customRenderLayers = new CustomRenderLayers();

	private static float scale = 0.02f;

	public static final ThreadLocal<VertexConsumer> isEnchanted = ThreadLocal.withInitial(() -> null);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		customRenderLayers.addCustomRenderLayer(Identifier.of(MOD_ID,"cutoutlayer"), cutoutLayer);
		customRenderLayers.addCustomRenderLayer(Identifier.of(MOD_ID,"solidlayer"), solidLayer);
	}

	public static float getScale()
	{
		return scale;
	}

	public static RenderLayer getOrCreateArmorRenderLayer(Identifier identifier)
	{
		RenderLayer output = customRenderLayers.getCustomRenderLayer(identifier);
		if(output != null)
		{
			return output;
		}
		return customRenderLayers.addCustomRenderLayer(identifier, createArmorRenderLayer(identifier));
	}
}