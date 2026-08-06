package net.grongubbe.tengoku.client.scene.camera;

import net.grongubbe.tengoku.client.scene.components.TransformComponent;
import org.joml.Matrix4f;

import java.util.Objects;

public final class Camera {
    private final TransformComponent transform = new TransformComponent();

    private final Frustum frustum = new Frustum();
    private final Matrix4f viewProjection = new Matrix4f();

    private Projection projection;
    private boolean frustumDirty = true;

    public Camera(Projection projection) {
        this.projection = Objects.requireNonNull(projection);
    }

    public TransformComponent transform() {
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

        frustumDirty = true;
    }

    public Frustum frustum() {
        if (frustumDirty) {
            rebuildFrustum();
        }

        return frustum;
    }

    public void resize(int width, int height) {
        if (height <= 0) {
            return;
        }

        projection.resize(width, height);

        frustumDirty = true;
    }

    private void rebuildFrustum() {
        projection(viewProjection);

        viewProjection.mul(view(new Matrix4f()));

        frustum.set(viewProjection);

        frustumDirty = false;
    }
}