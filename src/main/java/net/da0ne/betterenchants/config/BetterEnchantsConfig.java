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
import net.minecraft.util.math.ColorHelper;

/**
 * Taken from webspeak
 */
public class BetterEnchantsConfig {
    public static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("better-enchantment-glint.json");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean enabled = true;
    public boolean effect_armor = false;
    public boolean effect_special_item = true;
    public boolean armor_double_sided = false;
    public boolean use_original_special_item_uv = false;
    public boolean item_render_solid = false;
    public boolean armor_render_solid = true;
    public boolean special_item_render_solid = false;
    public float[] outline_color = {0.827f,0.592f,0.973f};
    public float[] custom_uv = {0,0};

    public float outline_size = 0.02f;

    public float getScale()
    {
        return outline_size;
    }

    public boolean getItemRenderSolid()
    {
        return item_render_solid;
    }

    public boolean getArmorRenderSolid()
    {
        return armor_render_solid;
    }

    public boolean getSpecialRenderSolid()
    {
        return special_item_render_solid;
    }

    public boolean getEnabled()
    {
        return enabled;
    }

    public float[] getOutlineColor()
    {
        return outline_color;
    }

    public int getOutlineColorAsInt()
    {
        float[] outlineColorFloat = getOutlineColor();
        return ColorHelper.withAlpha(0,ColorHelper.getArgb((int)(outlineColorFloat[0]*255), (int)(outlineColorFloat[1]*255), (int)(outlineColorFloat[2]*255)));
    }

    public void setOutlineColorAsInt(int color)
    {
        color = ColorHelper.withAlpha(255, color);
        float[] newOutlineColor = new float[3];
        newOutlineColor[0] = ColorHelper.getRedFloat(color);
        newOutlineColor[1] = ColorHelper.getGreenFloat(color);
        newOutlineColor[2] = ColorHelper.getBlueFloat(color);
        outline_color = newOutlineColor;
    }

    public boolean useOriginalUVs(boolean isArmor)
    {
        if(isArmor)
        {
            return true;
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
