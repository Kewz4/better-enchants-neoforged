package net.da0ne.betterenchants.mixin;

import net.da0ne.betterenchants.BetterEnchants;
import net.da0ne.betterenchants.util.VertexHelper;
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

    @Inject(method = "renderCuboid", at = @At("HEAD"))
    private void Da0ne$renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, int color, CallbackInfo ci)
    {
        if(!BetterEnchants.getConfig().getEnabled())
        {
            return;
        }
        if(BetterEnchants.isEnchanted.get() != null){

            boolean isArmor = BetterEnchants.isArmor.get();
            if((isArmor && !BetterEnchants.getConfig().shouldRenderArmor()) || (!isArmor && !BetterEnchants.getConfig().shouldRenderSpecialItems()))
            {
                return;
            }
            float scale = BetterEnchants.getConfig().getScale();

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
                            float[] uvs = BetterEnchants.getConfig().getCustomOrCurrentUV(verts[i].u(), verts[i].v(), isArmor);
                            VertexHelper.packVertexData(vertexData, i, vertPoses[i], uvs[0], uvs[1]);
                        }

                        BakedQuad enchantmentQuad = new BakedQuad(VertexHelper.flip(vertexData), -1, Direction.fromVector((int)quad.direction().x, (int)quad.direction().y, (int)quad.direction().z, Direction.NORTH), null, false, light);

                        BetterEnchants.isEnchanted.get().quad(entry, enchantmentQuad, 1f, 1f, 1f, 0.5f, 0, 0);
                    }

                    if(isArmor && BetterEnchants.getConfig().renderArmorDoubleSided())
                    {
                        faceVec.mul(-1);
                        for(Vector3f dir : cardinalDirs)
                        {
                            Vector3f[] vertPoses = VertexHelper.growFace(defaultVerts, dir, faceVec);

                            int[] vertexData = new int[vertPoses.length*8];
                            for(int i = 0; i < vertPoses.length; i++)
                            {
                                float[] uvs = BetterEnchants.getConfig().getCustomOrCurrentUV(verts[i].u(), verts[i].v(), isArmor);
                                VertexHelper.packVertexData(vertexData, i, vertPoses[i], uvs[0], uvs[1]);
                            }
                            BakedQuad enchantmentQuad = new BakedQuad(vertexData, -1, Direction.fromVector((int)quad.direction().x, (int)quad.direction().y, (int)quad.direction().z, Direction.NORTH), null, false, light);
                            BetterEnchants.isEnchanted.get().quad(entry, enchantmentQuad, 1f, 1f, 1f, 0.5f, 0, 0);
                        }
                    }
                }
            }
        }
    }
}
