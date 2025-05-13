package net.da0ne.betterenchants.mixin;

import com.mojang.logging.LogUtils;
import net.da0ne.betterenchants.VertexHelper;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReceiver;

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

import java.util.ArrayList;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Unique
    private static final ThreadLocal<Boolean> isEnchanted = ThreadLocal.withInitial(() -> false);
    //Lnet/minecraft/client/render/VertexConsumer
    @Inject(method = "renderItem(Lnet/minecraft/item/ModelTransformationMode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II[ILnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/item/ItemRenderState$Glint;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemModel(Lnet/minecraft/client/render/model/BakedModel;[IIILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;)V"))
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
            isEnchanted.set(true);
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
        isEnchanted.set(false);
    }

    @ModifyReceiver(method = "renderBakedItemQuads", at =  @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;quad(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/model/BakedQuad;FFFFII)V"))
    private static VertexConsumer Da0ne$renderBakedItemQuads(VertexConsumer receiver, MatrixStack.Entry matrixEntry, BakedQuad quad, float red, float green, float blue, float f, int i, int j){
        if(isEnchanted.get()) {
            float scale = 1.05f;

            int[] vertexData = quad.getVertexData().clone();
            Vec3i intVec = quad.getFace().getVector();
            Vector3f dirVec = new Vector3f(intVec.getX(), intVec.getY(), intVec.getZ());
            dirVec.mul(scale - 1f);

            Vector3f center = new Vector3f();
            Vector3f[] dirs = VertexHelper.getVertexPos(vertexData);
            for (Vector3f vert : dirs) {
                center.add(vert);
            }
            center.div(dirs.length);



            Vector3f[] vertPoses = new Vector3f[dirs.length];

            for(Vector3f dir : dirs){
                //get the difference between the dirrections
                Vector3f newDir = new Vector3f(dir);
                newDir.sub(center);
                newDir.mul(scale);
                newDir.add(center);
                //set it to teh difference
                newDir.sub(dir);
                for (int vertInterator = 0; vertInterator < dirs.length; vertInterator++) {
                    Vector3f vert = new Vector3f(dirs[vertInterator]);

                    vert.add(newDir);
                    vert.add(dirVec);

                    vertPoses[vertInterator] = vert;
                }
                VertexHelper.setVertexData(vertexData, vertPoses);

                BakedQuad enchantmentQuad = new BakedQuad(vertexData, -1, quad.getFace().getOpposite(), null, false, quad.getLightEmission());

                receiver.quad(matrixEntry, enchantmentQuad, 0.627f, 0.125f, 0.94f, 0.5f, 0, 0);
            }
        }
        return receiver;
    }
}
