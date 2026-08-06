package net.grongubbe.tengoku.client.scene.camera;

import net.grongubbe.tengoku.client.asset.bounds.BoundingVolume;
import org.joml.Matrix4f;

import java.util.Objects;

public final class Frustum {
    private final Plane[] planes = {
            new Plane(),
            new Plane(),
            new Plane(),
            new Plane(),
            new Plane(),
            new Plane()
    };

    public void set(Matrix4f matrix) {
        Objects.requireNonNull(matrix, "matrix");

        planes[0].set(
                matrix.m03() - matrix.m00(),
                matrix.m13() - matrix.m10(),
                matrix.m23() - matrix.m20(),
                matrix.m33() - matrix.m30()
        );

        planes[1].set(
                matrix.m03() + matrix.m00(),
                matrix.m13() + matrix.m10(),
                matrix.m23() + matrix.m20(),
                matrix.m33() + matrix.m30()
        );

        planes[2].set(
                matrix.m03() + matrix.m01(),
                matrix.m13() + matrix.m11(),
                matrix.m23() + matrix.m21(),
                matrix.m33() + matrix.m31()
        );

        planes[3].set(
                matrix.m03() - matrix.m01(),
                matrix.m13() - matrix.m11(),
                matrix.m23() - matrix.m21(),
                matrix.m33() - matrix.m31()
        );

        planes[4].set(
                matrix.m03() - matrix.m02(),
                matrix.m13() - matrix.m12(),
                matrix.m23() - matrix.m22(),
                matrix.m33() - matrix.m32()
        );

        planes[5].set(
                matrix.m03() + matrix.m02(),
                matrix.m13() + matrix.m12(),
                matrix.m23() + matrix.m22(),
                matrix.m33() + matrix.m32()
        );
    }

    public boolean intersects(BoundingVolume volume) {
        Objects.requireNonNull(volume, "volume");

        for (Plane plane : planes) {
            if (!volume.intersects(plane.normal(), plane.distance())) {
                return false;
            }
        }

        return true;
    }
}