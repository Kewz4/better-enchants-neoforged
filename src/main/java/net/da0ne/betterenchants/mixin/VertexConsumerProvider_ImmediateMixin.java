package net.da0ne.betterenchants.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.da0ne.betterenchants.mixin_acessors.RenderLayerAcessor;
import net.da0ne.betterenchants.mixin_acessors.VertexConsumerProvider_ImmediateAcessor;
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

import java.util.HashSet;
import java.util.SequencedMap;
import java.util.Set;

@Mixin(VertexConsumerProvider.Immediate.class)
public class VertexConsumerProvider_ImmediateMixin implements VertexConsumerProvider_ImmediateAcessor {
    @Unique
    private int mask_dirty = 0;
    @Unique
    private int solid_dirty = 0;

    @Shadow
    @Final
    protected SequencedMap<RenderLayer, BufferAllocator> layerBuffers;

    @Shadow
    public void draw(RenderLayer layer){}

    @Inject(method = "draw()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;drawCurrentLayer()V"))
    private void Da0ne$drawBeforeCustom(CallbackInfo ci)
    {
        for (RenderLayer renderLayer : this.layerBuffers.keySet()) {
            if(((RenderLayerAcessor)renderLayer).Da0ne$shouldDrawBeforeCustom(renderLayer)){
                draw(renderLayer);
            }
        }
    }

    @ModifyExpressionValue(method = "draw()V", at = @At(value = "INVOKE", target = "Ljava/util/SequencedMap;keySet()Ljava/util/Set;"))
    private Set<RenderLayer> Da0ne$removeDrawLoops(Set<RenderLayer> original)
    {
        Set<RenderLayer> copiedSet = new HashSet<>(original);
        for(RenderLayer renderLayer : original)
        {
            if(((RenderLayerAcessor)renderLayer).Da0ne$shouldDrawBeforeCustom(renderLayer)) {
                copiedSet.remove(renderLayer);
            }
        }
        return copiedSet;
    }

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
