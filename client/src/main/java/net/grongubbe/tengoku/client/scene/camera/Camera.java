package net.grongubbe.tengoku.client.scene.camera;

import net.grongubbe.tengoku.client.scene.components.TransformComponent;
import org.joml.Matrix4f;

import java.util.Objects;

public final class Camera {
    private final Frustum frustum = new Frustum();
    private final Matrix4f viewProjection = new Matrix4f();

    private Projection projection;

    public Camera(Projection projection) {
        this.projection = Objects.requireNonNull(projection, "projection");
    }

    public Matrix4f view(TransformComponent transform, Matrix4f destination) {
        Objects.requireNonNull(transform, "transform");
        Objects.requireNonNull(destination, "destination");

        return destination.set(transform.matrix(new Matrix4f())).invert();
    }

    public Matrix4f projection(Matrix4f destination) {
        Objects.requireNonNull(destination, "destination");

        return projection.matrix(destination);
    }

    public void setProjection(Projection projection) {
        this.projection = Objects.requireNonNull(projection, "projection");
    }

    public Frustum frustum(TransformComponent transform) {
        Objects.requireNonNull(transform, "transform");

        projection(viewProjection);

        viewProjection.mul(view(transform, new Matrix4f()));

        frustum.set(viewProjection);

        return frustum;
    }

    public void resize(int width, int height) {
        if (height <= 0) {
            return;
        }

        projection.resize(width, height);
    }
}