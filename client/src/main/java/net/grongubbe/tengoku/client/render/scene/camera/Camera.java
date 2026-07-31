package net.grongubbe.tengoku.client.render.scene.camera;

import net.grongubbe.tengoku.client.render.scene.Transform;
import org.joml.Matrix4f;

import java.util.Objects;

public final class Camera {
    private final Transform transform = new Transform();

    private Projection projection;

    public Camera(Projection projection) {
        this.projection = Objects.requireNonNull(projection);
    }

    public Transform transform() {
        return transform;
    }

    public Matrix4f view(Matrix4f destination) {
        return destination.set(transform.matrix(new Matrix4f())).invert();
    }

    public Matrix4f projection(Matrix4f destination) {
        return projection.matrix(destination);
    }

    public Projection projection() {
        return projection;
    }

    public void setProjection(Projection projection) {
        this.projection = Objects.requireNonNull(projection);
    }
}