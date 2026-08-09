package net.grongubbe.tengoku.client.render;

import org.joml.Vector3f;

public class RenderingConstants {
    public static final Vector3f AMBIENT_COLOR = new Vector3f(1.0f);
    public static final float AMBIENT_INTENSITY = 0.2f;

    public static final int MAX_POINT_LIGHTS = 16;

    public static final int DIRECTIONAL_SHADOW_MAP_SIZE = 2048;

    public static final float DIRECTIONAL_SHADOW_DISTANCE = 50.0f;
    public static final float DIRECTIONAL_SHADOW_SIZE = 20.0f;
    public static final float DIRECTIONAL_SHADOW_NEAR = 0.1f;
    public static final float DIRECTIONAL_SHADOW_FAR = 100.0f;

    private RenderingConstants() {
    }
}
