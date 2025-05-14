package net.da0ne.betterenchants.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.da0ne.betterenchants.BetterEnchants;
import net.da0ne.betterenchants.mixin_acessors.VertexConsumerProvider_ImmediateAcessor;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.SequencedMap;

@Mixin(BufferBuilderStorage.class)
public class BufferBuilderStorageMixin {
    //@Shadow
    //private static void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> builderStorage, RenderLayer layer){}

    //@Inject(method = "method_54639", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilderStorage;assignBufferBuilder(Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;Lnet/minecraft/client/render/RenderLayer;)V", ordinal = 5))
    //private void Da0ne$LambdaMapInject(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> map, CallbackInfo ci)
    //{
    //    assignBufferBuilder(map, BetterEnchants.armorCutoutLayer);
    //    assignBufferBuilder(map, BetterEnchants.cutoutLayer);
    //    assignBufferBuilder(map, BetterEnchants.solidLayer);
    //}

    @ModifyReturnValue(method = "getEntityVertexConsumers", at = @At("RETURN"))
    private VertexConsumerProvider.Immediate Da0ne$getEntityVertexConsumers(VertexConsumerProvider.Immediate original)
    {
        VertexConsumerProvider_ImmediateAcessor originalCast = ((VertexConsumerProvider_ImmediateAcessor)original);
        var enchantGlintLayer = RenderLayer.getArmorEntityGlint();
        var buffers = originalCast.Da0ne$getLayerBuffers();
        if(originalCast.Da0ne$getDirty() != BetterEnchants.customRenderLayers.getDirty() && buffers.containsKey(enchantGlintLayer)){
            originalCast.Da0ne$setDirty(BetterEnchants.customRenderLayers.getDirty());
            SequencedMap<RenderLayer, BufferAllocator> clonedBuffer = new Object2ObjectLinkedOpenHashMap<>(buffers);
            buffers.clear();
            for(var set : clonedBuffer.entrySet())
            {
                if(set.getKey() == enchantGlintLayer)
                {
                    for(RenderLayer layer : BetterEnchants.customRenderLayers.renderLayers())
                    {
                        buffers.put(layer, new BufferAllocator(layer.getExpectedBufferSize()));
                    }
                }
                buffers.put(set.getKey(), set.getValue());
            }
        }
        return original;
    }
}
