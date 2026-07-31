package net.grongubbe.tengoku.client.render.scene.camera.projection;

import net.grongubbe.tengoku.client.render.scene.camera.Projection;
import org.joml.Matrix4f;

public final class OrthographicProjection implements Projection {
    private float width;
    private float height;

    public OrthographicProjection(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Matrix4f matrix(Matrix4f destination) {
        float aspect = width / height;

        return destination.setOrtho(-aspect, aspect, -1.0f, 1.0f, -1.0f, 1.0f);
    }

    public void resize(float width, float height) {
        this.width = width;
        this.height = height;
    }
}