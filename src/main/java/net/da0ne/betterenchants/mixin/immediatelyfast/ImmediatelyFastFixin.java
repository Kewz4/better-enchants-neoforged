package net.da0ne.betterenchants.mixin.immediatelyfast;

import net.da0ne.betterenchants.mixin_accessors.RenderLayerAccessor;
import net.da0ne.betterenchants.mixin_accessors.VertexConsumerProvider_ImmediateAccessor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.raphimc.immediatelyfast.feature.core.BatchableBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BatchableBufferSource.class)
public class ImmediatelyFastFixin {
    @Shadow
    public void drawDirect(RenderLayer layer) {}

    //I don't know why immediatelyfast doesn't just run it's code in the override and then call the super method. but whatever. Reimplement the VertexConsumerProvider.Immediate mixin
    @Inject(method = "draw()V", at = @At(value = "INVOKE", target = "Lnet/raphimc/immediatelyfast/feature/core/BatchableBufferSource;drawCurrentLayer()V"))
    private void Da0ne$drawBeforeCustom(CallbackInfo ci)
    {
        for (RenderLayer renderLayer : ((VertexConsumerProvider_ImmediateAccessor)this).Da0ne$getLayerBuffers().keySet()) {
            if(((RenderLayerAccessor)renderLayer).Da0ne$shouldDrawBeforeCustom()){
                drawDirect(renderLayer);
            }
        }
    }
}
