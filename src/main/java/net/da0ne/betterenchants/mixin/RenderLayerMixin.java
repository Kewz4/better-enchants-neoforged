package net.da0ne.betterenchants.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.da0ne.betterenchants.mixin_acessors.RenderLayerAcessor;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderLayer.class)
public class RenderLayerMixin implements RenderLayerAcessor {

    @ModifyReturnValue(method = "areVerticesNotShared", at = @At("RETURN"))
    private boolean Da0ne$areVerticesNotShared(boolean original){
        if(drawBeforeCustom)
        {
            return false;
        }
        return original;
    }

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
