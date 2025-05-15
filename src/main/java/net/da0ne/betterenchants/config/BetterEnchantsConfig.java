package net.da0ne.betterenchants.config;

import java.io.BufferedWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.da0ne.betterenchants.BetterEnchants;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Taken from webspeak
 */
public class BetterEnchantsConfig {
    public static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("better-enchants.json");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean enabled = true;
    public boolean effect_armor = true;
    public boolean effect_special_item = true;
    public boolean armor_double_sided = true;
    public boolean use_original_armor_uv = true;
    public boolean use_original_special_item_uv = false;
    public float[] custom_uv = {0,0};

    public float outline_size = 0.02f;

    public float getScale()
    {
        return outline_size;
    }

    public boolean getEnabled()
    {
        return enabled;
    }

    public boolean useOriginalUVs(boolean isArmor)
    {
        if(isArmor)
        {
            return use_original_armor_uv;
        }
        return use_original_special_item_uv;
    }

    public float[] getCustomUVs()
    {
        return custom_uv;
    }

    public float[] getCustomOrCurrentUV(float u, float v, boolean isArmor)
    {
        if(!useOriginalUVs(isArmor))
        {
            return getCustomUVs();
        }
        return new float[]{u,v};
    }

    public boolean renderArmorDoubleSided()
    {
        return armor_double_sided;
    }

    public boolean shouldRenderArmor()
    {
        return effect_armor;
    }

    public boolean shouldRenderSpecialItems()
    {
        return effect_special_item;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static BetterEnchantsConfig fromJson(String json) {
        return GSON.fromJson(json, BetterEnchantsConfig.class);
    }

    public static BetterEnchantsConfig fromJson(Reader reader) {
        return GSON.fromJson(reader, BetterEnchantsConfig.class);
    }

    /**
     * Asynchronously save the BetterEnchants config to file.
     * @return A future that completes when the config is saved.
     */
    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(() -> {
            try(BufferedWriter writer = Files.newBufferedWriter(CONFIG_FILE)) {
                writer.write(toJson());
            } catch (Exception e) {
                BetterEnchants.LOGGER.error("Error saving BetterEnchants config.", e);
                throw new CompletionException(e);
            }
        });
    }
}
