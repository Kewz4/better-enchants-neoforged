package net.da0ne.betterenchants.mixin_acessors;

import net.minecraft.client.render.RenderLayer;

public interface RenderLayerAcessor {
    public boolean Da0ne$shouldDrawBeforeCustom(RenderLayer us);
    public void Da0ne$setDrawBeforeCustom(RenderLayer us, boolean newDrawBeforeCustom);
}
