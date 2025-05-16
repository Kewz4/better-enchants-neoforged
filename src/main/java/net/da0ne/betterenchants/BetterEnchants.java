package net.da0ne.betterenchants;

import net.da0ne.betterenchants.config.BetterEnchantsConfig;
import net.da0ne.betterenchants.mixin_acessors.RenderLayerAcessor;
import net.da0ne.betterenchants.util.CustomRenderLayers;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class BetterEnchants implements ModInitializer {
	public static final String MOD_ID = "better-enchants";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static final ShaderProgramKey cutoutShaderKey = new ShaderProgramKey(Identifier.of(MOD_ID,"core/cutout"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, Defines.EMPTY);
	public static final RenderPhase.ShaderProgram cutoutShader = new RenderPhase.ShaderProgram(cutoutShaderKey);
	public static final ShaderProgramKey solidShaderKey = new ShaderProgramKey(Identifier.of(MOD_ID,"core/solid"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, Defines.EMPTY);
	public static final RenderPhase.ShaderProgram solidShader = new RenderPhase.ShaderProgram(solidShaderKey);

	public static final RenderLayer enchantCutoutLayer = RenderLayer.of(
			"custom_enchants_cutout",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			786432,
			true,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.program(cutoutShader)
					.writeMaskState(RenderLayer.DEPTH_MASK)
					.texture(RenderLayer.BLOCK_ATLAS_TEXTURE)
					.cull(RenderLayer.ENABLE_CULLING)
					.build(true)
	);

	public static final RenderLayer enchantSolidLayer = RenderLayer.of(
			"custom_enchants_cutout",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			786432,
			true,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.program(solidShader)
					.writeMaskState(RenderLayer.DEPTH_MASK)
					.cull(RenderLayer.ENABLE_CULLING)
					//.texture(RenderLayer.BLOCK_ATLAS_TEXTURE)
					.build(true)
	);

	public static final RenderLayer solidCutoutLayer = RenderLayer.of(
			"custom_enchants_cutout",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			786432,
			true,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.program(cutoutShader)
					.writeMaskState(RenderLayer.COLOR_MASK)
					.texture(RenderLayer.BLOCK_ATLAS_TEXTURE)
					.cull(RenderLayer.ENABLE_CULLING)
					.build(true)
	);

	public static final RenderLayer solidSolidLayer = RenderLayer.of(
			"custom_enchants_cutout",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			786432,
			true,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.program(solidShader)
					.transparency(RenderLayer.NO_TRANSPARENCY)
					.cull(RenderLayer.ENABLE_CULLING)
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.overlay(RenderLayer.DISABLE_OVERLAY_COLOR)
					.writeMaskState(RenderLayer.COLOR_MASK)
					.build(true)
	);

	private static RenderLayer createEnchantmentArmorRenderLayer(Identifier texture){
		return RenderLayer.of(
			"custom_enchants_armor",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
				786432,
			true,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.program(cutoutShader)
					.cull(RenderLayer.ENABLE_CULLING)
					.texture(new RenderPhase.Texture(texture, TriState.FALSE, false))
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.writeMaskState(RenderLayer.DEPTH_MASK)
					.cull(RenderLayer.ENABLE_CULLING)
					.build(true));
	}

	private static RenderLayer createSolidArmorRenderLayer(Identifier texture){
		RenderLayer layer = RenderLayer.of(
				"custom_enchants_armor",
				VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
				VertexFormat.DrawMode.QUADS,
				786432,
				true,
				false,
				RenderLayer.MultiPhaseParameters.builder()
						.program(cutoutShader)
						.texture(new RenderPhase.Texture(texture, TriState.FALSE, false))
						.transparency(RenderLayer.NO_TRANSPARENCY)
						.cull(RenderLayer.ENABLE_CULLING)
						.lightmap(RenderLayer.ENABLE_LIGHTMAP)
						.overlay(RenderLayer.DISABLE_OVERLAY_COLOR)
						.writeMaskState(RenderLayer.COLOR_MASK)
						.build(true));
		((RenderLayerAcessor)layer).Da0ne$setDrawBeforeCustom(solidSolidLayer, true);
		return layer;
	}

	public static final CustomRenderLayers enchantmentMaskLayers = new CustomRenderLayers();
	public static final CustomRenderLayers solidOutlineLayers = new CustomRenderLayers();

	private static BetterEnchantsConfig config;

	public static BetterEnchantsConfig getConfig()
	{
		return config;
	}

	public static final ThreadLocal<VertexConsumer> isEnchanted = ThreadLocal.withInitial(() -> null);
	public static final ThreadLocal<Boolean> isArmor = ThreadLocal.withInitial(() -> false);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		loadConfig();

		((RenderLayerAcessor)solidSolidLayer).Da0ne$setDrawBeforeCustom(solidSolidLayer, true);

		enchantmentMaskLayers.addCustomRenderLayer(Identifier.of(MOD_ID,"cutoutlayer"), enchantCutoutLayer);
		enchantmentMaskLayers.addCustomRenderLayer(Identifier.of(MOD_ID,"solidlayer"), enchantSolidLayer);
		solidOutlineLayers.addCustomRenderLayer(Identifier.of(MOD_ID,"cutoutlayer"), solidCutoutLayer);
		solidOutlineLayers.addCustomRenderLayer(Identifier.of(MOD_ID,"solidlayer"), solidSolidLayer);
	}

	public static RenderLayer getOrCreateEnchantmentArmorRenderLayer(Identifier identifier)
	{
		RenderLayer output = enchantmentMaskLayers.getCustomRenderLayer(identifier);
		if(output != null)
		{
			return output;
		}
		return enchantmentMaskLayers.addCustomRenderLayer(identifier, createEnchantmentArmorRenderLayer(identifier));
	}

	public static RenderLayer getOrCreateSolidArmorRenderLayer(Identifier identifier)
	{
		RenderLayer output = solidOutlineLayers.getCustomRenderLayer(identifier);
		if(output != null)
		{
			return output;
		}
		return solidOutlineLayers.addCustomRenderLayer(identifier, createSolidArmorRenderLayer(identifier));
	}

	private static void loadConfig() {
		Path configFile = BetterEnchantsConfig.CONFIG_FILE;
		if (Files.exists(configFile)) {
			try(BufferedReader reader = Files.newBufferedReader(configFile)) {
				config = BetterEnchantsConfig.fromJson(reader);
			} catch (Exception e) {
				LOGGER.error("Error loading WebSpeak config file. Default values will be used for this session.", e);
				config = new BetterEnchantsConfig();
			}
		} else {
			config = new BetterEnchantsConfig();
		}

		// Immedietly save config to file to update any fields that may have changed.
		config.saveAsync();
	}
}