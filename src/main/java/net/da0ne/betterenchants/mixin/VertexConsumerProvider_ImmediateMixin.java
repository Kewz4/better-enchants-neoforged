package net.da0ne.betterenchants.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.da0ne.betterenchants.mixin_accessors.RenderLayerAccessor;
import net.da0ne.betterenchants.mixin_accessors.VertexConsumerProvider_ImmediateAccessor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.SequencedMap;

@Mixin(VertexConsumerProvider.Immediate.class)
public class VertexConsumerProvider_ImmediateMixin implements VertexConsumerProvider_ImmediateAccessor {
    @Shadow
    @Final
    protected SequencedMap<RenderLayer, BufferAllocator> layerBuffers;

    @Shadow
    public void draw(RenderLayer layer){}

    @Inject(method = "drawCurrentLayer", at = @At("HEAD"))
    private void Da0ne$drawBeforeCustom(CallbackInfo ci)
    {
        for (RenderLayer renderLayer : this.layerBuffers.keySet()) {
            if(((RenderLayerAccessor)renderLayer).Da0ne$shouldDrawBeforeCustom()){
                draw(renderLayer);
            }
        }
    }

    @Unique
    private int mask_dirty = 0;
    @Unique
    private int solid_dirty = 0;

    @Override
    public SequencedMap<RenderLayer, BufferAllocator> Da0ne$getLayerBuffers() {
        return layerBuffers;
    }

    @Override
    public int Da0ne$getMaskDirty() {
        return mask_dirty;
    }

    @Override
    public void Da0ne$setMaskDirty(int newDirty) {
        mask_dirty = newDirty;
    }

    @Override
    public int Da0ne$getSolidDirty() {
        return solid_dirty;
    }

    @Override
    public void Da0ne$setSolidDirty(int newDirty) {
       solid_dirty = newDirty;
    }
}
