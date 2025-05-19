package net.da0ne.betterenchants.mixin_accessors;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.BufferAllocator;

import java.util.SequencedMap;

public interface VertexConsumerProvider_ImmediateAccessor {
    public abstract SequencedMap<RenderLayer, BufferAllocator> Da0ne$getLayerBuffers();
    public abstract int Da0ne$getMaskDirty();
    public abstract void Da0ne$setMaskDirty(int newDirty);
    public abstract int Da0ne$getSolidDirty();
    public abstract void Da0ne$setSolidDirty(int newDirty);
}
