package net.da0ne.betterenchants.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.da0ne.betterenchants.BetterEnchants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;

public class ClothConfigScreenFactory {
    public static Screen makeConfig(Screen parent)
    {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.betterenchants.config"))
                .setSavingRunnable(BetterEnchants.getConfig()::saveAsync);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory render = builder.getOrCreateCategory(Text.translatable("category.betterenchants.render"));
        ConfigCategory armor = builder.getOrCreateCategory(Text.translatable("category.betterenchants.armor"));
        ConfigCategory special = builder.getOrCreateCategory(Text.translatable("category.betterenchants.special"));
        ConfigCategory uvs = builder.getOrCreateCategory(Text.translatable("category.betterenchants.uvs"));

        render.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.betterenchants.render.enabled"), BetterEnchants.getConfig().getEnabled())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("tooltip.betterenchants.render.enabled"))
                .setSaveConsumer(newValue -> BetterEnchants.getConfig().enabled = newValue)
                .build());
        render.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.betterenchants.render.solid"), BetterEnchants.getConfig().getItemRenderSolid())
                .setDefaultValue(false)
                .setTooltip(Text.translatable("tooltip.betterenchants.render.solid"))
                .setSaveConsumer(newValue -> BetterEnchants.getConfig().item_render_solid = newValue)
                .build());
        render.addEntry(entryBuilder.startColorField(Text.translatable("option.betterenchants.render.color"), BetterEnchants.getConfig().getOutlineColorAsInt())
                .setDefaultValue(ColorHelper.getArgb(0,210, 150, 248))
                .setTooltip(Text.translatable("tooltip.betterenchants.render.color"))
                .setSaveConsumer(newValue -> BetterEnchants.getConfig().setOutlineColorAsInt(newValue))
                .build());

        render.addEntry(entryBuilder.startIntSlider(Text.translatable("option.betterenchants.render.size"), (int)(BetterEnchants.getConfig().getScale()*1000f), 0, 30)
                .setDefaultValue(20)
                .setTooltip(Text.translatable("tooltip.betterenchants.render.size"))
                .setSaveConsumer(newValue -> BetterEnchants.getConfig().outline_size = ((float)newValue)/1000f )
                .build());

        armor.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.betterenchants.armor.render"), BetterEnchants.getConfig().shouldRenderArmor())
                .setDefaultValue(false)
                .setTooltip(Text.translatable("tooltip.betterenchants.armor.render.line1").append("\n").append(Text.translatable("tooltip.betterenchants.armor.render.line2").formatted(Formatting.RED)))
                .setSaveConsumer(newValue -> BetterEnchants.getConfig().effect_armor = newValue)
                .build());
        armor.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.betterenchants.armor.solid"), BetterEnchants.getConfig().getArmorRenderSolid())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("tooltip.betterenchants.armor.solid"))
                .setSaveConsumer(newValue -> BetterEnchants.getConfig().armor_render_solid = newValue)
                .build());
        armor.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.betterenchants.armor.originaluv"), BetterEnchants.getConfig().use_original_armor_uv)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("tooltip.betterenchants.armor.originaluv"))
                .setSaveConsumer(newValue -> BetterEnchants.getConfig().use_original_armor_uv = newValue)
                .build());
        armor.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.betterenchants.armor.doubleside"), BetterEnchants.getConfig().renderArmorDoubleSided())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("tooltip.betterenchants.armor.doubleside"))
                .setSaveConsumer(newValue -> BetterEnchants.getConfig().armor_double_sided = newValue)
                .build());

        special.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.betterenchants.special.render"), BetterEnchants.getConfig().shouldRenderSpecialItems())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("tooltip.betterenchants.special.render"))
                .setSaveConsumer(newValue -> BetterEnchants.getConfig().effect_special_item = newValue)
                .build());
        special.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.betterenchants.special.solid"), BetterEnchants.getConfig().getSpecialRenderSolid())
                .setDefaultValue(false)
                .setTooltip(Text.translatable("tooltip.betterenchants.special.solid1").append("\n").append(Text.translatable("tooltip.betterenchants.special.solid2").formatted(Formatting.RED)))
                .setSaveConsumer(newValue -> BetterEnchants.getConfig().special_item_render_solid = newValue)
                .build());
        special.addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.betterenchants.special.originaluv"), BetterEnchants.getConfig().use_original_special_item_uv)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("tooltip.betterenchants.special.originaluv"))
                .setSaveConsumer(newValue -> BetterEnchants.getConfig().use_original_special_item_uv = newValue)
                .build());

        uvs.addEntry(entryBuilder.startFloatField(Text.translatable("option.betterenchants.uvs.u"), BetterEnchants.getConfig().getCustomUVs()[0])
                .setDefaultValue(0)
                .setTooltip(Text.translatable("tooltip.betterenchants.uvs.u"))
                .setSaveConsumer(newValue -> BetterEnchants.getConfig().custom_uv[0] = newValue)
                .build());
        uvs.addEntry(entryBuilder.startFloatField(Text.translatable("option.betterenchants.uvs.v"), BetterEnchants.getConfig().getCustomUVs()[1])
                .setDefaultValue(0)
                .setTooltip(Text.translatable("tooltip.betterenchants.uvs.v"))
                .setSaveConsumer(newValue -> BetterEnchants.getConfig().custom_uv[1] = newValue)
                .build());

        return builder.build();
    }
}
