package net.da0ne.betterenchants.mixin;

import com.mojang.logging.LogUtils;
import net.da0ne.betterenchants.VertexHelper;
import net.minecraft.util.math.Direction;
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
import java.util.Arrays;

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
            float scale = 0.02f;

            int[] vertexData = quad.getVertexData().clone();
            Vector3f[] defaultVerts = VertexHelper.getVertexPos(vertexData);

            if(defaultVerts.length == 4) {
                Vector3f center = new Vector3f();
                for (Vector3f vert : defaultVerts) {
                    center.add(vert);
                }
                center.div(defaultVerts.length);

                Vector3f corner1 = defaultVerts[0];
                Vector3f corner2 = defaultVerts[1];
                corner1.sub(center);
                corner2.sub(center);

                Vector3f side1 = new Vector3f(corner1);
                side1.add(corner2);

                Vector3f side2 = new Vector3f(corner1);
                side2.sub(corner2);

                side1.normalize();
                side2.normalize();

                //this is localDiagonal. We don't realocate cause that's not efficent
                Vector3f localDiagonal = side1;
                localDiagonal.add(side2);
                localDiagonal.mul(scale);
                //localDiagonal.add(corner1);

                Vector3f otherLocal = new Vector3f(localDiagonal).reflect(side2);

                //LogUtils.getLogger().info("localDiag: " + localDiagonal + ", side1: " + side1 + ", corner1: " + corner1 + ", center: " + center);

                Vector3f[] cardinalDirs = {new Vector3f(localDiagonal), new Vector3f(otherLocal), localDiagonal.mul(-1), otherLocal.mul(-1)};

                corner1.add(center);
                corner2.add(center);

                Vec3i intVec = quad.getFace().getVector();
                Vector3f faceVec = new Vector3f(intVec.getX(), intVec.getY(), intVec.getZ());
                faceVec.mul(scale);

                Vector3f[] vertPoses = new Vector3f[defaultVerts.length];
                for(Vector3f dir : cardinalDirs){

                    for (int vertInterator = 0; vertInterator < defaultVerts.length; vertInterator++)
                    {
                        Vector3f vert = new Vector3f(defaultVerts[vertInterator]);
                        vert.add(dir);
                        vert.add(faceVec);
                        vertPoses[vertInterator] = vert;
                    }

                    VertexHelper.setVertexData(vertexData, vertPoses);

                    BakedQuad enchantmentQuad = new BakedQuad(VertexHelper.flip(vertexData), -1, quad.getFace().getOpposite(), null, false, quad.getLightEmission());
                    //LogUtils.getLogger().info("normal: " + enchantmentQuad.getFace() + ", normalVector: " + enchantmentQuad.getFace().getVector());

                    receiver.quad(matrixEntry, enchantmentQuad, 1f, 1f, 1f, 0.5f, 0, 0);
                }
            }
        }
        return receiver;
    }
}
