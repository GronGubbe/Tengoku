package net.grongubbe.tengoku.client.scene.light;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Objects;

public abstract class Light {
    private final Vector3f color = new Vector3f(1.0f);
    private float intensity = 1.0f;

    public Vector3fc color() {
        return color;
    }

    public float intensity() {
        return intensity;
    }

    public void setColor(Vector3fc color) {
        Objects.requireNonNull(color, "color");

        if (color.x() < 0.0f || color.y() < 0.0f || color.z() < 0.0f) {
            throw new IllegalArgumentException("Light color components must be >= 0");
        }

        this.color.set(color);
    }

    public void setColor(float red, float green, float blue) {
        if (red < 0.0f || green < 0.0f || blue < 0.0f) {
            throw new IllegalArgumentException("Light color components must be >= 0");
        }

        color.set(red, green, blue);
    }

    public void setIntensity(float intensity) {
        if (intensity < 0.0f) {
            throw new IllegalArgumentException("Light intensity must be >= 0");
        }

        this.intensity = intensity;
    }
}
