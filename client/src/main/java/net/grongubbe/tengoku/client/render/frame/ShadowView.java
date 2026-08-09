package net.grongubbe.tengoku.client.render.frame;

import org.joml.Matrix4f;

import java.util.Objects;

public final class ShadowView {
    private final Matrix4f view;
    private final Matrix4f projection;

    public ShadowView(Matrix4f view, Matrix4f projection) {
        this.view = new Matrix4f(Objects.requireNonNull(view, "view"));
        this.projection = new Matrix4f(Objects.requireNonNull(projection, "projection"));
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