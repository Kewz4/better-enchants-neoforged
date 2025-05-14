package net.da0ne.betterenchants.mixin_acessors;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.BufferAllocator;

import java.util.SequencedMap;

public interface VertexConsumerProvider_ImmediateAcessor {
    public abstract SequencedMap<RenderLayer, BufferAllocator> Da0ne$getLayerBuffers();
    public abstract int Da0ne$getDirty();
    public abstract void Da0ne$setDirty(int newDirty);
}
