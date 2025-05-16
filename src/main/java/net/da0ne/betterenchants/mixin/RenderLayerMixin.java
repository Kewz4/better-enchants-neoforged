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
    private static Map<RenderLayer, Boolean> drawBeforeCustom = new WeakHashMap<>();

    @Override
    public boolean Da0ne$shouldDrawBeforeCustom(RenderLayer us) {
        Boolean value = drawBeforeCustom.get(us);
        if(value != null)
        {
            return value;
        }
        return false;
    }

    @Override
    public void Da0ne$setDrawBeforeCustom(RenderLayer us, boolean newDrawBeforeCustom) {
        drawBeforeCustom.put(us, newDrawBeforeCustom);
    }
}
