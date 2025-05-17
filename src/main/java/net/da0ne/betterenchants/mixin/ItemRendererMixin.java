package net.da0ne.betterenchants.mixin;

import net.da0ne.betterenchants.BetterEnchants;
import net.da0ne.betterenchants.util.VertexHelper;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;

import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @ModifyArgs(method = "renderItem(Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II[ILjava/util/List;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/item/ItemRenderState$Glint;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderBakedItemQuads(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Ljava/util/List;[III)V"))
    private static void Da0ne$renderBakedItemQuads(Args args){

        MatrixStack matrices = args.get(0);
        List<BakedQuad> quads = args.get(2);

        if(!BetterEnchants.getConfig().getEnabled())
        {
            return;
        }
        if(BetterEnchants.isEnchanted.get() != null) {
            float scale = BetterEnchants.getConfig().getScale();
            MatrixStack.Entry matrixEntry = matrices.peek();

            float[] outlineColor = BetterEnchants.getConfig().getOutlineColor();
            for (BakedQuad quad : quads) {
                int[] vertexData = quad.vertexData().clone();
                Vector3f[] defaultVerts = VertexHelper.getVertexPos(vertexData);

                Vec3i intVec = quad.face().getVector();
                Vector3f faceVec = new Vector3f(intVec.getX(), intVec.getY(), intVec.getZ());
                faceVec.mul(scale);

                Vector3f[] cardinalDirs = VertexHelper.getFaceCardinalDirs(defaultVerts, scale);
                if (cardinalDirs != null) {
                    for (Vector3f dir : cardinalDirs) {

                        Vector3f[] vertPoses = VertexHelper.growFace(defaultVerts, dir, faceVec);

                        VertexHelper.setVertexData(vertexData, vertPoses);

                        BakedQuad enchantmentQuad = new BakedQuad(VertexHelper.flip(vertexData), -1, quad.face().getOpposite(), null, false, 100);

                        BetterEnchants.isEnchanted.get().quad(matrixEntry, enchantmentQuad, outlineColor[0], outlineColor[1], outlineColor[2], 1, 0, 0);
                    }
                }
            }
        }
    }
}
