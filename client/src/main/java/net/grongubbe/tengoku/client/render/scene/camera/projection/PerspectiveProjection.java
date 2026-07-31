package net.grongubbe.tengoku.client.render.scene.camera.projection;

import net.grongubbe.tengoku.client.render.scene.camera.Projection;
import org.joml.Matrix4f;

public final class PerspectiveProjection implements Projection {
    private float fieldOfView;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;

    public PerspectiveProjection(float fieldOfView, float aspectRatio, float nearPlane, float farPlane) {
        this.fieldOfView = fieldOfView;
        this.aspectRatio = aspectRatio;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
    }

    @Override
    public Matrix4f matrix(Matrix4f destination) {
        return destination.setPerspective(fieldOfView, aspectRatio, nearPlane, farPlane);
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
}