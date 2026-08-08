package net.grongubbe.tengoku.client.render.frame;

import net.grongubbe.tengoku.client.scene.camera.Camera;
import net.grongubbe.tengoku.client.scene.components.TransformComponent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Objects;

public final class RenderView {
    private final Matrix4f view;
    private final Matrix4f projection;
    private final Vector3f position;

    public RenderView(Camera camera, TransformComponent transform) {
        Objects.requireNonNull(camera, "camera");

        this.view = camera.view(transform, new Matrix4f());
        this.projection = camera.projection(new Matrix4f());
        this.position = transform.position(new Vector3f());
    }

    public Matrix4f view(Matrix4f destination) {
        Objects.requireNonNull(destination, "destination");
        return destination.set(view);
    }

    public Matrix4f projection(Matrix4f destination) {
        Objects.requireNonNull(destination, "destination");
        return destination.set(projection);
    }

    public Vector3f position(Vector3f destination) {
        Objects.requireNonNull(destination, "destination");
        return destination.set(position);
    }
}