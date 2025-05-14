package net.da0ne.betterenchants.mixin;

import net.da0ne.betterenchants.BetterEnchants;
import net.da0ne.betterenchants.VertexHelper;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPart.Cuboid.class)
public class ModelPart_CuboidMixin {
    @Shadow
    @Final
    public ModelPart.Quad[] sides;

    @Inject(method = "renderCuboid", at = @At("RETURN"))
    private void Da0ne$renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, int color, CallbackInfo ci)
    {
        if(BetterEnchants.isEnchanted.get() != null){

            float scale = BetterEnchants.getScale();

            for(var quad : sides)
            {
                var verts = quad.vertices();
                Vector3f[] defaultVerts = new Vector3f[verts.length];
                for(int i = 0; i < defaultVerts.length; i++){
                    defaultVerts[i] = new Vector3f(verts[i].pos());
                    defaultVerts[i].div(16.0F);
                }

                Vector3f faceVec = new Vector3f(quad.direction());
                faceVec.normalize();
                faceVec.mul(scale);

                Vector3f[] cardinalDirs = VertexHelper.getFaceCardinalDirs(defaultVerts, scale);
                if (cardinalDirs != null) {
                    for (Vector3f dir : cardinalDirs) {

                        Vector3f[] vertPoses = VertexHelper.growFace(defaultVerts, dir, faceVec);

                        int[] vertexData = new int[vertPoses.length*8];
                        for(int i = 0; i < vertPoses.length; i++)
                        {
                            //0, 0 as UV look better, but should be less accurate. They are not less accurate cause I have no idea how minecraft rendering works
                            VertexHelper.packVertexData(vertexData, i, vertPoses[i], verts[i].u(), verts[i].v());
                        }

                        BakedQuad enchantmentQuad = new BakedQuad(VertexHelper.flip(vertexData), -1, Direction.fromVector((int)quad.direction().x, (int)quad.direction().y, (int)quad.direction().z, Direction.NORTH), null, false, light);
                        //LogUtils.getLogger().info("normal: " + enchantmentQuad.getFace() + ", normalVector: " + enchantmentQuad.getFace().getVector());

                        BetterEnchants.isEnchanted.get().quad(entry, enchantmentQuad, 1f, 1f, 1f, 0.5f, 0, 0);
                        //receiver.quad(matrixEntry, enchantmentQuad, 1f, 1f, 1f, 0.5f, 0, 0);
                    }
                }
            }
        }
    }
}
