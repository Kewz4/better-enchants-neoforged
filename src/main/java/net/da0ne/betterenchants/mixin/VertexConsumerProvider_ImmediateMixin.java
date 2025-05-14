package net.da0ne.betterenchants.mixin;

import net.da0ne.betterenchants.mixin_acessors.VertexConsumerProvider_ImmediateAcessor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.SequencedMap;

@Mixin(VertexConsumerProvider.Immediate.class)
public class VertexConsumerProvider_ImmediateMixin implements VertexConsumerProvider_ImmediateAcessor {
    @Unique
    private int dirty = 0;

    @Shadow
    @Final
    protected SequencedMap<RenderLayer, BufferAllocator> layerBuffers;

    @Override
    public SequencedMap<RenderLayer, BufferAllocator> Da0ne$getLayerBuffers() {
        return layerBuffers;
    }

    @Override
    public int Da0ne$getDirty() {
        return dirty;
    }

    @Override
    public void Da0ne$setDirty(int newDirty) {
        dirty = newDirty;
    }
}
