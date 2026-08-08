package net.grongubbe.tengoku.client.render.frame;

import net.grongubbe.tengoku.client.scene.light.Light;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;

public final class RenderLight {
    private final Light light;
    private final Vector3f position;
    private final Quaternionf rotation;

    public RenderLight(Light light, Vector3f position, Quaternionf rotation) {
        this.light = Objects.requireNonNull(light, "light");
        this.position = new Vector3f(Objects.requireNonNull(position, "position"));
        this.rotation = new Quaternionf(Objects.requireNonNull(rotation, "rotation"));
    }

    public Light light() {
        return light;
    }

    public Vector3f position(Vector3f destination) {
        Objects.requireNonNull(destination, "destination");

        return destination.set(position);
    }

    public Quaternionf rotation(Quaternionf destination) {
        Objects.requireNonNull(destination, "destination");

        return destination.set(rotation);
    }
}