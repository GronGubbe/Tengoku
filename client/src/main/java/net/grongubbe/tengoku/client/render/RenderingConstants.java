package net.grongubbe.tengoku.client.render;

import org.joml.Vector3f;

public class RenderingConstants {
    public static final Vector3f AMBIENT_COLOR = new Vector3f(1.0f);
    public static final float AMBIENT_INTENSITY = 0.2f;

    public static final int MAX_POINT_LIGHTS = 16;

    private RenderingConstants() {
    }
}
