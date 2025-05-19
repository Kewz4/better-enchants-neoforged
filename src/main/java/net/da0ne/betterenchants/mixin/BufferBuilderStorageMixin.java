package net.da0ne.betterenchants.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.da0ne.betterenchants.BetterEnchants;
import net.da0ne.betterenchants.mixin_accessors.RenderLayerAccessor;
import net.da0ne.betterenchants.mixin_accessors.VertexConsumerProvider_ImmediateAccessor;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
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
        VertexConsumerProvider_ImmediateAccessor originalCast = ((VertexConsumerProvider_ImmediateAccessor)original);
        var enchantGlintLayer = RenderLayer.getArmorEntityGlint();
        var buffers = originalCast.Da0ne$getLayerBuffers();
        if((originalCast.Da0ne$getMaskDirty() != BetterEnchants.ENCHANTMENT_MASK_LAYERS.getDirty() && buffers.containsKey(enchantGlintLayer)) || (originalCast.Da0ne$getSolidDirty() != BetterEnchants.SOLID_OUTLINE_LAYERS.getDirty() && buffers.containsKey(TexturedRenderLayers.getEntitySolid()))){
            originalCast.Da0ne$setMaskDirty(BetterEnchants.ENCHANTMENT_MASK_LAYERS.getDirty());
            originalCast.Da0ne$setSolidDirty(BetterEnchants.SOLID_OUTLINE_LAYERS.getDirty());
            SequencedMap<RenderLayer, BufferAllocator> clonedBuffer = new Object2ObjectLinkedOpenHashMap<>(buffers);
            buffers.clear();
            for(var set : clonedBuffer.entrySet())
            {
                if(!BetterEnchants.ENCHANTMENT_MASK_LAYERS.containsRenderLayer(set.getKey()) && !BetterEnchants.SOLID_OUTLINE_LAYERS.containsRenderLayer(set.getKey()))
                {
                    if(set.getKey() == TexturedRenderLayers.getEntitySolid())
                    {
                        for(RenderLayer layer : BetterEnchants.SOLID_OUTLINE_LAYERS.renderLayers())
                        {
                            if(!((RenderLayerAccessor)layer).Da0ne$notLayerBuffer()){
                                buffers.put(layer, new BufferAllocator(layer.getExpectedBufferSize()));
                            }
                        }
                    }
                    if(set.getKey() == enchantGlintLayer)
                    {
                        for(RenderLayer layer : BetterEnchants.ENCHANTMENT_MASK_LAYERS.renderLayers())
                        {
                            buffers.put(layer, new BufferAllocator(layer.getExpectedBufferSize()));
                        }
                    }
                    buffers.put(set.getKey(), set.getValue());
                }
            }
        }
        return original;
    }
}
