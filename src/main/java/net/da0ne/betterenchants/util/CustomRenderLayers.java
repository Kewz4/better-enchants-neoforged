package net.da0ne.betterenchants.util;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import java.util.*;

public class CustomRenderLayers {
    private final Map<Identifier, RenderLayer> customRenderLayers = new HashMap<>();
    private int dirty = 0;

    public int getDirty()
    {
        return dirty;
    }

    private void setDirty(int dirty)
    {
        this.dirty = dirty;
    }

    public RenderLayer addCustomRenderLayer(Identifier identifier, RenderLayer layer)
    {
        RenderLayer output = customRenderLayers.put(identifier, layer);
        if(output == null)
        {
            setDirty(getDirty()+1);
        }
        return output;
    }

    public RenderLayer getCustomRenderLayer(Identifier identifier)
    {
        return customRenderLayers.get(identifier);
    }

    public Iterable<RenderLayer> renderLayers()
    {
        return customRenderLayers.values();
    }
}
