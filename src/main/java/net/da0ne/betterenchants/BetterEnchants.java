package net.da0ne.betterenchants;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.da0ne.betterenchants.config.BetterEnchantsConfig;
import net.da0ne.betterenchants.mixin_accessors.RenderLayerAccessor;
import net.da0ne.betterenchants.util.CustomRenderLayers;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.layer.BufferSourceWrapper;
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

	public static final boolean IRIS_LOADED = FabricLoader.getInstance().isModLoaded("iris");

    //TODO: move all these to a seperate class.
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

	public static final RenderLayer ENCHANT_CUTOUT_LAYER = RenderLayer.of(
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

	public static final RenderLayer ENCHANT_SOLID_LAYER = RenderLayer.of(
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

	public static final RenderLayer SOLID_CUTOUT_LAYER = RenderLayer.of(
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

	public static final RenderLayer SOLID_SOLID_LAYER = RenderLayer.of(
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

		RenderLayerAccessor acessor = (RenderLayerAccessor)layer;

		acessor.Da0ne$setDrawBeforeCustom(true);
		if( FabricLoader.getInstance().isModLoaded("immediatelyfast")) {
			acessor.Da0ne$setNotLayerBuffer(true);
		}
		if(IRIS_LOADED) {
			acessor.Da0ne$setArmor(true);
		}
		return layer;
	}

	public static final CustomRenderLayers ENCHANTMENT_MASK_LAYERS = new CustomRenderLayers();
	public static final CustomRenderLayers SOLID_OUTLINE_LAYERS = new CustomRenderLayers();

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

		((RenderLayerAccessor) SOLID_SOLID_LAYER).Da0ne$setDrawBeforeCustom(true);
		((RenderLayerAccessor) SOLID_CUTOUT_LAYER).Da0ne$setDrawBeforeCustom(true);

		ENCHANTMENT_MASK_LAYERS.addCustomRenderLayer(Identifier.of(MOD_ID,"cutoutlayer"), ENCHANT_CUTOUT_LAYER);
		ENCHANTMENT_MASK_LAYERS.addCustomRenderLayer(Identifier.of(MOD_ID,"solidlayer"), ENCHANT_SOLID_LAYER);
		SOLID_OUTLINE_LAYERS.addCustomRenderLayer(Identifier.of(MOD_ID,"cutoutlayer"), SOLID_CUTOUT_LAYER);
		SOLID_OUTLINE_LAYERS.addCustomRenderLayer(Identifier.of(MOD_ID,"solidlayer"), SOLID_SOLID_LAYER);
	}

	public static RenderLayer getOrCreateEnchantmentArmorRenderLayer(Identifier identifier)
	{
		RenderLayer output = ENCHANTMENT_MASK_LAYERS.getCustomRenderLayer(identifier);
		if(output != null)
		{
			return output;
		}
		return ENCHANTMENT_MASK_LAYERS.addCustomRenderLayer(identifier, createEnchantmentArmorRenderLayer(identifier));
	}

	public static RenderLayer getOrCreateSolidArmorRenderLayer(Identifier identifier)
	{
		RenderLayer output = SOLID_OUTLINE_LAYERS.getCustomRenderLayer(identifier);
		if(output != null)
		{
			return output;
		}
		return SOLID_OUTLINE_LAYERS.addCustomRenderLayer(identifier, createSolidArmorRenderLayer(identifier));
	}

	public static VertexConsumerProvider.Immediate getImmediate(VertexConsumerProvider vertexConsumers)
	{
		VertexConsumerProvider.Immediate immediate = null;
		if(getIrisOriginal(vertexConsumers) instanceof VertexConsumerProvider.Immediate im) {
			immediate = im;
		}
		return immediate;
	}

	private static VertexConsumerProvider getIrisOriginal(VertexConsumerProvider vertexConsumerProvider)
	{
		if(IRIS_LOADED && vertexConsumerProvider instanceof BufferSourceWrapper wrapper)
		{
			return getIrisOriginal(wrapper.getOriginal());
		}
		return vertexConsumerProvider;
	}

	private static void loadConfig() {
		Path configFile = BetterEnchantsConfig.CONFIG_FILE;
		if (Files.exists(configFile)) {
			try(BufferedReader reader = Files.newBufferedReader(configFile)) {
				config = BetterEnchantsConfig.fromJson(reader);
			} catch (Exception e) {
				LOGGER.error("Error loading BetterEnchants config file. Default values will be used for this session.", e);
				config = new BetterEnchantsConfig();
			}
		} else {
			config = new BetterEnchantsConfig();
		}

		// Immedietly save config to file to update any fields that may have changed.
		config.saveAsync();
	}
}