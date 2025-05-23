package net.da0ne.betterenchants.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.da0ne.betterenchants.mixin_accessors.RenderLayerAccessor;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderLayer.class)
public class RenderLayerMixin implements RenderLayerAccessor {

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

    @Unique
    private boolean notLayerBuffer = false;

    @Unique
    private boolean armor = false;

    @Override
    public boolean Da0ne$shouldDrawBeforeCustom() {
        return drawBeforeCustom;
    }

    @Override
    public void Da0ne$setDrawBeforeCustom(boolean newDrawBeforeCustom) {
        drawBeforeCustom = newDrawBeforeCustom;
    }

    @Override
    public boolean Da0ne$notLayerBuffer() {
        return notLayerBuffer;
    }

    @Override
    public void Da0ne$setNotLayerBuffer(boolean newNotLayerBuffer) {
        notLayerBuffer = newNotLayerBuffer;
    }

    @Override
    public boolean Da0ne$isArmor() {
        return armor;
    }

    @Override
    public void Da0ne$setArmor(boolean newArmor) {
        armor = newArmor;
    }
}
