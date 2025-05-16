package net.da0ne.betterenchants.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.da0ne.betterenchants.BetterEnchants;
import net.minecraft.client.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @ModifyReceiver(method = "method_62214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw(Lnet/minecraft/client/render/RenderLayer;)V", ordinal = 13))
    private VertexConsumerProvider.Immediate Da0ne$RenderMainArmor(VertexConsumerProvider.Immediate receiver, RenderLayer layer)
    {
        for(var customLayer : BetterEnchants.enchantmentMaskLayers.renderLayers())
        {
            receiver.draw(customLayer);
        }
        return receiver;
    }

    @ModifyReceiver( method = "method_62214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw(Lnet/minecraft/client/render/RenderLayer;)V", ordinal = 3))
    private VertexConsumerProvider.Immediate Da0ne$RenderMainItemTranslucent(VertexConsumerProvider.Immediate receiver, RenderLayer layer)
    {
        for(var customLayer : BetterEnchants.solidOutlineLayers.renderLayers())
        {
            receiver.draw(customLayer);
        }
        return receiver;
    }
}
