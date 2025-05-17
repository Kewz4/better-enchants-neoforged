package net.da0ne.betterenchants;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.da0ne.betterenchants.config.BetterEnchantsConfig;
import net.da0ne.betterenchants.mixin_acessors.RenderLayerAcessor;
import net.da0ne.betterenchants.util.CustomRenderLayers;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.gl.RenderPipelines;
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


	public static final RenderPipeline.Snippet OUTLINE_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_FOG_OFFSET_SNIPPET)
			.withVertexShader(Identifier.of(MOD_ID,"core/outline"))
			.withFragmentShader(Identifier.of(MOD_ID,"core/outline"))
			.withSampler("Sampler0")
			.withSampler("Sampler2")
			.withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS)
			.buildSnippet();

	/*public static final ShaderProgramKey cutoutShaderKey = new ShaderProgramKey(, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, Defines.EMPTY);
	public static final ShaderProgram cutoutShader = new RenderPhase.ShaderProgram(cutoutShaderKey);
	public static final ShaderProgramKey solidShaderKey = new ShaderProgramKey(, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, Defines.EMPTY);
	public static final RenderPhase.ShaderProgram solidShader = new RenderPhase.ShaderProgram(solidShaderKey);*/

	public static final RenderPipeline CUTOUT_PIPELINE_DEPTH = RenderPipelines.register(
			RenderPipeline.builder(OUTLINE_SNIPPET)
					.withLocation(Identifier.of(MOD_ID,"pipeline/cutout"))
					.withDepthWrite(true)
					.withColorWrite(false)
					.withCull(true)
					.withShaderDefine("ALPHA_CUTOUT", 0.1F)
					.build()
	);

	public static final RenderPipeline SOLID_PIPELINE_DEPTH = RenderPipelines.register(
			RenderPipeline.builder(OUTLINE_SNIPPET)
					.withDepthWrite(true)
					.withColorWrite(false)
					.withCull(true)
					.withLocation(Identifier.of(MOD_ID,"pipeline/solid"))
					.build()
	);

	public static final RenderPipeline CUTOUT_PIPELINE_COLOR = RenderPipelines.register(
			RenderPipeline.builder(OUTLINE_SNIPPET)
					.withLocation(Identifier.of(MOD_ID,"pipeline/cutout"))
					.withDepthWrite(false)
					.withColorWrite(true)
					.withCull(true)
					.withShaderDefine("ALPHA_CUTOUT", 0.1F)
					.build()
	);

	public static final RenderPipeline SOLID_PIPELINE_COLOR = RenderPipelines.register(
			RenderPipeline.builder(OUTLINE_SNIPPET)
					.withDepthWrite(false)
					.withColorWrite(true)
					.withCull(true)
					.withLocation(Identifier.of(MOD_ID,"pipeline/solid"))
					.build()
	);

	public static final RenderLayer enchantCutoutLayer = RenderLayer.of(
			"custom_enchants_cutout",
			786432,
			true,
			false,
			CUTOUT_PIPELINE_DEPTH,
			RenderLayer.MultiPhaseParameters.builder()
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.texture(RenderLayer.BLOCK_ATLAS_TEXTURE)
					.build(true)
	);

	public static final RenderLayer enchantSolidLayer = RenderLayer.of(
			"custom_enchants_cutout",
			786432,
			true,
			false,
			SOLID_PIPELINE_DEPTH,
			RenderLayer.MultiPhaseParameters.builder()
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					//.texture(RenderLayer.BLOCK_ATLAS_TEXTURE)
					.build(true)
	);

	public static final RenderLayer solidCutoutLayer = RenderLayer.of(
			"custom_enchants_cutout",
			786432,
			true,
			false,
			CUTOUT_PIPELINE_COLOR,
			RenderLayer.MultiPhaseParameters.builder()
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.texture(RenderLayer.BLOCK_ATLAS_TEXTURE)
					.build(true)
	);

	public static final RenderLayer solidSolidLayer = RenderLayer.of(
			"custom_enchants_cutout",
			786432,
			true,
			false,
			SOLID_PIPELINE_COLOR,
			RenderLayer.MultiPhaseParameters.builder()
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.overlay(RenderLayer.DISABLE_OVERLAY_COLOR)
					.build(true)
	);

	private static RenderLayer createEnchantmentArmorRenderLayer(Identifier texture){
		return RenderLayer.of(
			"custom_enchants_armor",
				786432,
			true,
			false,
			CUTOUT_PIPELINE_DEPTH,
			RenderLayer.MultiPhaseParameters.builder()
					.texture(new RenderPhase.Texture(texture, TriState.FALSE, false))
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.build(true));
	}

	private static RenderLayer createSolidArmorRenderLayer(Identifier texture){
		RenderLayer layer = RenderLayer.of(
				"custom_enchants_armor",
				786432,
				true,
				false,
				CUTOUT_PIPELINE_COLOR,
				RenderLayer.MultiPhaseParameters.builder()
						.texture(new RenderPhase.Texture(texture, TriState.FALSE, false))
						.lightmap(RenderLayer.ENABLE_LIGHTMAP)
						.overlay(RenderLayer.DISABLE_OVERLAY_COLOR)
						.build(true));
		((RenderLayerAcessor)layer).Da0ne$setDrawBeforeCustom(true);
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

		((RenderLayerAcessor)solidSolidLayer).Da0ne$setDrawBeforeCustom(true);
		((RenderLayerAcessor)solidCutoutLayer).Da0ne$setDrawBeforeCustom(true);

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