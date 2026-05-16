package net.grongubbe.tengoku.client;

public class ColorCycle {

    private static float triangle(float x) {
        x = x % 1.0f;
        return 1.0f - Math.abs(2.0f * x - 1.0f);
    }

    public static float[] getRGB(int input, int period) {
        // Normalize input into 0, 1
        float t = (input % period) / (float) period;

        // Phase-shift each color by 1/3 cycle
        float red   = triangle(t);
        float green = triangle(t + 1.0f / 3.0f);
        float blue  = triangle(t + 2.0f / 3.0f);

        return new float[]{red, green, blue};
    }
}