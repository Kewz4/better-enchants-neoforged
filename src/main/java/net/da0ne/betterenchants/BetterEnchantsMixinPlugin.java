package net.da0ne.betterenchants;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class BetterEnchantsMixinPlugin implements IMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if(mixinClassName.contains("net.da0ne.betterenchants.mixin.immediatelyfast"))
        {
            if(FabricLoader.getInstance().isModLoaded("immediatelyfast"))
            {
                System.out.println("better-enchants: ImmediatelyFast found. Applying mixin: " + mixinClassName);
                return true;
            }
            else
            {
                return false;
            }
        }
        if(mixinClassName.contains("net.da0ne.betterenchants.mixin.iris"))
        {
            if(FabricLoader.getInstance().isModLoaded("iris"))
            {
                System.out.println("better-enchants: Iris found. Applying mixin: " + mixinClassName);
                return true;
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
