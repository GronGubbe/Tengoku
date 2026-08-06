package net.grongubbe.tengoku.client.asset.bounds;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Objects;

public final class BoundingSphere implements BoundingVolume {
    private final Vector3f center = new Vector3f();
    private float radius;

    public BoundingSphere(Vector3f center, float radius) {
        Objects.requireNonNull(center, "center");

        if (radius < 0) {
            throw new IllegalArgumentException("radius must be positive");
        }

        this.center.set(center);
        this.radius = radius;
    }

    public Vector3f center(Vector3f destination) {
        Objects.requireNonNull(destination, "destination");

        return destination.set(center);
    }

    public float radius() {
        return radius;
    }

    @Override
    public BoundingSphere translated(Vector3fc translation) {
        Objects.requireNonNull(translation, "translation");

        return new BoundingSphere(new Vector3f(center).add(translation), radius);
    }

    public void setCenter(Vector3f center) {
        Objects.requireNonNull(center, "center");

        this.center.set(center);
    }

    public void setRadius(float radius) {
        if (radius < 0) {
            throw new IllegalArgumentException("radius must be positive");
        }

        this.radius = radius;
    }

    @Override
    public boolean intersects(Vector3f normal, float distance) {
        float signedDistance = normal.dot(center) + distance;

        return signedDistance >= -radius;
    }
}