package net.da0ne.betterenchants.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.da0ne.betterenchants.BetterEnchants;
import net.da0ne.betterenchants.mixin_accessors.RenderLayerAccessor;
import net.minecraft.client.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @ModifyReceiver(method = "method_62214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw(Lnet/minecraft/client/render/RenderLayer;)V", ordinal = 13))
    private VertexConsumerProvider.Immediate Da0ne$RenderMainArmor(VertexConsumerProvider.Immediate receiver, RenderLayer layer)
    {
        for(var customLayer : BetterEnchants.ENCHANTMENT_MASK_LAYERS.renderLayers())
        {
            receiver.draw(customLayer);
        }
        return receiver;
    }

    @ModifyReceiver( method = "method_62214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw(Lnet/minecraft/client/render/RenderLayer;)V", ordinal = 3))
    private VertexConsumerProvider.Immediate Da0ne$RenderMainItemTranslucent(VertexConsumerProvider.Immediate receiver, RenderLayer layer)
    {
        for(var customLayer : BetterEnchants.SOLID_OUTLINE_LAYERS.renderLayers())
        {
            if(!((RenderLayerAccessor)customLayer).Da0ne$notLayerBuffer()) {
                receiver.draw(customLayer);
            }
        }
        return receiver;
    }
}
