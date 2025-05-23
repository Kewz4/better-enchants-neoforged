package net.da0ne.betterenchants.mixin_accessors;

//TODO: make it so that instead of a shouldDrawBeforeCustom each renderLayer can store the layer it should draw before, and use that. This should fix ALL strange rendering artifacts
public interface RenderLayerAccessor {
    public boolean Da0ne$shouldDrawBeforeCustom();
    public void Da0ne$setDrawBeforeCustom(boolean newDrawBeforeCustom);
    public boolean Da0ne$notLayerBuffer();
    public void Da0ne$setNotLayerBuffer(boolean newNotLayerBuffer);
}
