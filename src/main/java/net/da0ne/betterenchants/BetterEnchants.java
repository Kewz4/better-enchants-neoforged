package net.da0ne.betterenchants;

import net.da0ne.betterenchants.config.BetterEnchantsConfig;
import net.da0ne.betterenchants.mixin_accessors.RenderLayerAccessor;
import net.da0ne.betterenchants.util.CustomRenderLayers;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.layer.BufferSourceWrapper;
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



	//TODO: move all these to a seperate class.
	public static final ShaderProgramKey CUTOUT_SHADER_KEY = new ShaderProgramKey(Identifier.of(MOD_ID,"core/cutout"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, Defines.EMPTY);
	public static final RenderPhase.ShaderProgram CUTOUT_SHADER = new RenderPhase.ShaderProgram(CUTOUT_SHADER_KEY);
	public static final ShaderProgramKey SOLID_SHADER_KEY = new ShaderProgramKey(Identifier.of(MOD_ID,"core/solid"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, Defines.EMPTY);
	public static final RenderPhase.ShaderProgram SOLID_SHADER = new RenderPhase.ShaderProgram(SOLID_SHADER_KEY);

	public static final RenderLayer ENCHANT_CUTOUT_LAYER = RenderLayer.of(
			"custom_enchants_cutout",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			786432,
			true,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.program(CUTOUT_SHADER)
					.writeMaskState(RenderLayer.DEPTH_MASK)
					.texture(RenderLayer.BLOCK_ATLAS_TEXTURE)
					.cull(RenderLayer.ENABLE_CULLING)
					.build(true)
	);

	public static final RenderLayer ENCHANT_SOLID_LAYER = RenderLayer.of(
			"custom_enchants_cutout",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			786432,
			true,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.program(SOLID_SHADER)
					.writeMaskState(RenderLayer.DEPTH_MASK)
					.cull(RenderLayer.ENABLE_CULLING)
					//.texture(RenderLayer.BLOCK_ATLAS_TEXTURE)
					.build(true)
	);

	public static final RenderLayer SOLID_CUTOUT_LAYER = RenderLayer.of(
			"custom_enchants_cutout",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			786432,
			true,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.lightmap(RenderLayer.ENABLE_LIGHTMAP)
					.program(CUTOUT_SHADER)
					.writeMaskState(RenderLayer.COLOR_MASK)
					.texture(RenderLayer.BLOCK_ATLAS_TEXTURE)
					.cull(RenderLayer.ENABLE_CULLING)
					.build(true)
	);

	public static final RenderLayer SOLID_SOLID_LAYER = RenderLayer.of(
			"custom_enchants_cutout",
			VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
			VertexFormat.DrawMode.QUADS,
			786432,
			true,
			false,
			RenderLayer.MultiPhaseParameters.builder()
					.program(SOLID_SHADER)
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
					.program(CUTOUT_SHADER)
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
						.program(CUTOUT_SHADER)
						.texture(new RenderPhase.Texture(texture, TriState.FALSE, false))
						.transparency(RenderLayer.NO_TRANSPARENCY)
						.cull(RenderLayer.ENABLE_CULLING)
						.lightmap(RenderLayer.ENABLE_LIGHTMAP)
						.overlay(RenderLayer.DISABLE_OVERLAY_COLOR)
						.writeMaskState(RenderLayer.COLOR_MASK)
						.build(true));
		((RenderLayerAccessor)layer).Da0ne$setDrawBeforeCustom(true);
		if( FabricLoader.getInstance().isModLoaded("immediatelyfast")) {
			((RenderLayerAccessor) layer).Da0ne$setNotLayerBuffer(true);
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
		if(vertexConsumerProvider instanceof BufferSourceWrapper wrapper)
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