package net.da0ne.betterenchants.mixin;

import net.da0ne.betterenchants.BetterEnchants;
import net.da0ne.betterenchants.mixin_accessors.VertexConsumerProvider_ImmediateAccessor;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EquipmentRenderer.class)
public class EquipmentRendererMixin {
    @Unique
    private static final ThreadLocal<Identifier> textureIdentifier = ThreadLocal.withInitial(() -> null);

    @ModifyArg(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier Da0ne$RenderIdentifier(Identifier texture)
    {
        textureIdentifier.set(texture);
        return texture;
    }

    @ModifyArgs(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;getArmorGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;Z)Lnet/minecraft/client/render/VertexConsumer;"))
    private void Da0ne$renderEntry(Args args)
    {
        if(textureIdentifier.get() != null)
        {
            if(args.get(2)){
                if(!BetterEnchants.getConfig().getArmorRenderSolid()) {
                    RenderLayer layer = BetterEnchants.getOrCreateEnchantmentArmorRenderLayer(textureIdentifier.get());
                    if (layer != null && ((VertexConsumerProvider_ImmediateAccessor)(args.get(0))).Da0ne$getMaskDirty() == BetterEnchants.ENCHANTMENT_MASK_LAYERS.getDirty()) {
                        BetterEnchants.isArmor.set(true);

                        BetterEnchants.isEnchanted.set(ItemRenderer.getArmorGlintConsumer(args.get(0), layer, true));
                    }
                }
                else
                {
                    RenderLayer layer = BetterEnchants.getOrCreateSolidArmorRenderLayer(textureIdentifier.get());
                    //RenderLayer layer = RenderLayer.getArmorCutoutNoCull(textureIdentifier.get());
                    if (layer != null && ((VertexConsumerProvider_ImmediateAccessor)(args.get(0))).Da0ne$getSolidDirty() == BetterEnchants.SOLID_OUTLINE_LAYERS.getDirty()) {
                        BetterEnchants.isArmor.set(true);
                        BetterEnchants.isEnchanted.set(ItemRenderer.getArmorGlintConsumer(args.get(0), layer, false));
                    }
                }
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/util/Identifier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V", shift = At.Shift.AFTER))
    private void Da0ne$renderEntry(EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> assetKey, Model model, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, @Nullable Identifier texture, CallbackInfo ci)
    {
        if(BetterEnchants.getConfig().getArmorRenderSolid()) {
            RenderLayer layer = BetterEnchants.getOrCreateSolidArmorRenderLayer(textureIdentifier.get());
            if (layer != null&& ((VertexConsumerProvider_ImmediateAccessor)vertexConsumers).Da0ne$getSolidDirty() == BetterEnchants.SOLID_OUTLINE_LAYERS.getDirty()) {
                ItemRenderer.getArmorGlintConsumer(vertexConsumers, layer, false);
            }
        }
        textureIdentifier.remove();
        BetterEnchants.isArmor.remove();
        BetterEnchants.isEnchanted.remove();
    }
}
