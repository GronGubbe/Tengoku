package net.grongubbe.tengoku.client.scene.camera;

import org.joml.Vector3f;

public final class Plane {
    private final Vector3f normal = new Vector3f();
    private float distance;

    public Vector3f normal() {
        return normal;
    }

    public float distance() {
        return distance;
    }

    public void set(float x, float y, float z, float w) {
        normal.set(x, y, z);

        float length = normal.length();

        normal.div(length);
        distance = w / length;
    }
}