package net.da0ne.betterenchants.mixin;

import net.da0ne.betterenchants.BetterEnchants;
import net.da0ne.betterenchants.VertexHelper;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.item.ItemRenderState.Glint;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ModelTransformationMode;

import java.util.List;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    //Lnet/minecraft/client/render/VertexConsumer
    /*@Inject(method = "renderItem(Lnet/minecraft/item/ModelTransformationMode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II[ILnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/item/ItemRenderState$Glint;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemModel(Lnet/minecraft/client/render/model/BakedModel;[IIILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;)V"))
    private static void Da0ne$renderItem$INVOKE(
        ModelTransformationMode transformationMode,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		int[] tints,
		BakedModel model,
		RenderLayer layer,
		ItemRenderState.Glint glint,
        CallbackInfo ci
    )
    {
        if(glint != Glint.NONE)
        {
            //LogUtils.getLogger().info("layer: " + layer);
            BetterEnchants.isEnchanted.set(BetterEnchants.createOutlineVertexConsumer(vertexConsumers));
        }
    }
    
    @Inject(method = "renderItem(Lnet/minecraft/item/ModelTransformationMode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II[ILnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/item/ItemRenderState$Glint;)V", at = @At(value = "RETURN"))
    private static void Da0ne$renderItem$Return(
        ModelTransformationMode transformationMode,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		int[] tints,
		BakedModel model,
		RenderLayer layer,
		ItemRenderState.Glint glint,
        CallbackInfo ci
    )
    {
        BetterEnchants.isEnchanted.set(null);
    }*/

    @Inject(method = "renderBakedItemQuads", at = @At("RETURN"))//value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;quad(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/model/BakedQuad;FFFFII)V"))
    private static void Da0ne$renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, int[] tints, int light, int overlay, CallbackInfo ci){
        if(BetterEnchants.isEnchanted.get() != null) {
            float scale = BetterEnchants.getScale();
            MatrixStack.Entry matrixEntry = matrices.peek();

            for (BakedQuad quad : quads) {
                int[] vertexData = quad.getVertexData().clone();
                Vector3f[] defaultVerts = VertexHelper.getVertexPos(vertexData);

                Vec3i intVec = quad.getFace().getVector();
                Vector3f faceVec = new Vector3f(intVec.getX(), intVec.getY(), intVec.getZ());
                faceVec.mul(scale);

                Vector3f[] cardinalDirs = VertexHelper.getFaceCardinalDirs(defaultVerts, scale);
                if (cardinalDirs != null) {
                    for (Vector3f dir : cardinalDirs) {

                        Vector3f[] vertPoses = VertexHelper.growFace(defaultVerts, dir, faceVec);

                        VertexHelper.setVertexData(vertexData, vertPoses);

                        BakedQuad enchantmentQuad = new BakedQuad(VertexHelper.flip(vertexData), -1, quad.getFace().getOpposite(), null, false, quad.getLightEmission());
                        //LogUtils.getLogger().info("normal: " + enchantmentQuad.getFace() + ", normalVector: " + enchantmentQuad.getFace().getVector());

                        BetterEnchants.isEnchanted.get().quad(matrixEntry, enchantmentQuad, 1f, 1f, 1f, 0.5f, 0, 0);
                        //receiver.quad(matrixEntry, enchantmentQuad, 1f, 1f, 1f, 0.5f, 0, 0);
                    }
                }
            }
        }
    }
}
