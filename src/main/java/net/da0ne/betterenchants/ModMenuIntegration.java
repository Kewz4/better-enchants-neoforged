package net.da0ne.betterenchants;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.da0ne.betterenchants.config.ClothConfigScreenFactory;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2"))
        {
            return ClothConfigScreenFactory::makeConfig;
        }
        return (parent) -> null;
    }
}
