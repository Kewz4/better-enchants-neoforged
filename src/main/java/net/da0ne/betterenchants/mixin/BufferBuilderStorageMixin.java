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
import org.spongepowered.asm.mixin.injection.At;

import java.util.SequencedMap;

@Mixin(BufferBuilderStorage.class)
public class BufferBuilderStorageMixin {

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
