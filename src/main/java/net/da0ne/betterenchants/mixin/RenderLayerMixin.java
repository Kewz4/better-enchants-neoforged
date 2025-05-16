package net.da0ne.betterenchants.mixin;

import net.da0ne.betterenchants.mixin_acessors.RenderLayerAcessor;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin(RenderLayer.class)
public class RenderLayerMixin implements RenderLayerAcessor {
    @Unique
    private boolean drawBeforeCustom = false;

    @Override
    public boolean Da0ne$shouldDrawBeforeCustom() {
        return drawBeforeCustom;
    }

    @Override
    public void Da0ne$setDrawBeforeCustom(boolean newDrawBeforeCustom) {
        drawBeforeCustom = newDrawBeforeCustom;
    }
}
