package net.da0ne.betterenchants.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.mojang.logging.LogUtils;
import net.da0ne.betterenchants.BetterEnchants;
import net.minecraft.client.render.*;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @ModifyReceiver(method = "method_62214", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw(Lnet/minecraft/client/render/RenderLayer;)V", ordinal = 13))
    private VertexConsumerProvider.Immediate Da0ne$RenderMain(VertexConsumerProvider.Immediate receiver, RenderLayer layer)
    {
        //LogUtils.getLogger().info("layerDRAWN: " + layer.toString());
        receiver.draw(BetterEnchants.ourRenderLayer);
        return receiver;
    }
}
