package net.grongubbe.tengoku.client.render.frame;

import net.grongubbe.tengoku.client.render.scene.camera.Camera;
import org.joml.Matrix4f;

import java.util.Objects;

public final class RenderView {
    private final Matrix4f view;
    private final Matrix4f projection;

    public RenderView(Camera camera) {
        Objects.requireNonNull(camera, "camera");

        this.view = camera.view(new Matrix4f());
        this.projection = camera.projection(new Matrix4f());
    }

    public Matrix4f view(Matrix4f destination) {
        Objects.requireNonNull(destination, "destination");
        return destination.set(view);
    }

    public Matrix4f projection(Matrix4f destination) {
        Objects.requireNonNull(destination, "destination");
        return destination.set(projection);
    }
}