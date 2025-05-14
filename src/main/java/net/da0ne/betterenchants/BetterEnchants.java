package net.da0ne.betterenchants;

import net.fabricmc.api.ModInitializer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.TriState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterEnchants implements ModInitializer {
	public static final String MOD_ID = "better-enchants";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final RenderLayer ourRenderLayer = RenderLayer.of(
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

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

	}
}