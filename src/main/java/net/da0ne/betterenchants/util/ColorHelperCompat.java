package net.da0ne.betterenchants.util;

/** Utility methods missing from older ColorHelper APIs. */
public class ColorHelperCompat {
    public static int withAlpha(int alpha, int color) {
        return (alpha << 24) | (color & 0xFFFFFF);
    }

    public static float getRedFloat(int color) {
        return ((color >> 16) & 0xFF) / 255.0F;
    }

    public static float getGreenFloat(int color) {
        return ((color >> 8) & 0xFF) / 255.0F;
    }

    public static float getBlueFloat(int color) {
        return (color & 0xFF) / 255.0F;
    }

    public static int getArgb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int getArgb(int red, int green, int blue) {
        return getArgb(255, red, green, blue);
    }
}
