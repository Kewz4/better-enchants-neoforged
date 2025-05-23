package net.da0ne.betterenchants.mixin.iris;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.logging.LogUtils;
import net.da0ne.betterenchants.BetterEnchants;
import net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.irisshaders.batchedentityrendering.impl.WrappableRenderType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(FullyBufferedMultiBufferSource.class)
public class FullyBufferedMultiBufferSourceMixin {
    @ModifyExpressionValue(method = "readyUp", at = @At(value = "INVOKE", target = "Lnet/irisshaders/batchedentityrendering/impl/ordering/RenderOrderManager;getRenderOrder()Ljava/util/List;"), remap = false)
    private List<RenderLayer> Da0ne$ModifyRenderOrder(List<RenderLayer> original)
    {
        List<RenderLayer> duplicate = new ArrayList<>(original.size());

        List<RenderLayer> encahntsRemoved = new ArrayList<>(original);
        List<RenderLayer> maskLayers = new ArrayList<>();
        List<RenderLayer> solidLayers = new ArrayList<>();

        RenderLayer unWrappedLayer;
        for(RenderLayer layer : original)
        {
            unWrappedLayer = layer;
            if(layer instanceof WrappableRenderType wrapped)
            {
                unWrappedLayer = wrapped.unwrap();
            }

            if(BetterEnchants.ENCHANTMENT_MASK_LAYERS.containsRenderLayer(unWrappedLayer))
            {
                maskLayers.add(layer);
                encahntsRemoved.remove(layer);
            }
            else if(BetterEnchants.SOLID_OUTLINE_LAYERS.containsRenderLayer(unWrappedLayer))
            {
                solidLayers.add(layer);
                encahntsRemoved.remove(layer);
            }
        }

        boolean hasAddedSolids = false;
        boolean hasAddedEnchantments = false;

        for(RenderLayer layer : encahntsRemoved)
        {
            unWrappedLayer = layer;
            if(layer instanceof WrappableRenderType wrapped)
            {
                unWrappedLayer = wrapped.unwrap();
            }
            if (!hasAddedEnchantments && (unWrappedLayer == RenderLayer.getGlint())) {
                hasAddedEnchantments = true;
                duplicate.addAll(maskLayers);
            }
            if(!hasAddedSolids && unWrappedLayer == TexturedRenderLayers.getItemEntityTranslucentCull())
            {
                hasAddedSolids = true;
                duplicate.addAll(solidLayers);
            }
            duplicate.add(layer);
        }
        if(!hasAddedSolids)
        {
            duplicate.addAll(solidLayers);
        }
        return duplicate;
    }
}
