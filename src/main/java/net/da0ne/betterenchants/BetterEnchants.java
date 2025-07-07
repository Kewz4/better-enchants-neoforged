package net.da0ne.betterenchants;

import net.da0ne.betterenchants.config.BetterEnchantsConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class BetterEnchants implements ModInitializer {
    public static final String MOD_ID = "better-enchants";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final boolean IRIS_LOADED = FabricLoader.getInstance().isModLoaded("iris");

    private static BetterEnchantsConfig config;

    @Override
    public void onInitialize() {
        loadConfig();
    }

    public static BetterEnchantsConfig getConfig() {
        return config;
    }

    private static void loadConfig() {
        Path configFile = BetterEnchantsConfig.CONFIG_FILE;
        if (Files.exists(configFile)) {
            try (BufferedReader reader = Files.newBufferedReader(configFile)) {
                config = BetterEnchantsConfig.fromJson(reader);
            } catch (Exception e) {
                LOGGER.error("Error loading BetterEnchants config file. Default values will be used for this session.", e);
                config = new BetterEnchantsConfig();
            }
        } else {
            config = new BetterEnchantsConfig();
        }
        config.saveAsync();
    }

    // Minimal stubs for compatibility with 1.21.1
    public static RenderLayer getOrCreateEnchantmentArmorRenderLayer(Identifier identifier) {
        return RenderLayer.getArmorEntityGlint();
    }

    public static RenderLayer getOrCreateSolidArmorRenderLayer(Identifier identifier) {
        return RenderLayer.getArmorEntityGlint();
    }

    public static VertexConsumerProvider.Immediate getImmediate(VertexConsumerProvider provider) {
        return provider instanceof VertexConsumerProvider.Immediate im ? im : null;
    }
}
