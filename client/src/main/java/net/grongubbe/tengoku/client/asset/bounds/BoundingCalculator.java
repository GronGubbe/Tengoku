package net.grongubbe.tengoku.client.asset.bounds;

import net.grongubbe.tengoku.client.asset.mesh.Mesh;
import net.grongubbe.tengoku.client.asset.model.ModelPart;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

public final class BoundingCalculator {
    private BoundingCalculator() {
    }

    public static BoundingVolume calculate(List<ModelPart> parts) {
        Objects.requireNonNull(parts, "parts");

        if (parts.isEmpty()) {
            throw new IllegalArgumentException("Cannot calculate bounds for empty model");
        }

        Vector3f minimum = new Vector3f(Float.POSITIVE_INFINITY);
        Vector3f maximum = new Vector3f(Float.NEGATIVE_INFINITY);

        for (ModelPart part : parts) {
            includeMesh(part.mesh(), minimum, maximum);
        }

        Vector3f center = new Vector3f(minimum).add(maximum).mul(0.5f);

        float radius = center.distance(maximum);

        return new BoundingSphere(center, radius);
    }

    private static void includeMesh(Mesh mesh, Vector3f minimum, Vector3f maximum) {
        ByteBuffer vertices = mesh.data().vertices().data();

        int stride = mesh.data().layout().stride();

        int positionOffset = mesh.data().layout().attributeOffset("position");

        for (int offset = 0; offset < vertices.capacity(); offset += stride) {
            float x = vertices.getFloat(offset + positionOffset);
            float y = vertices.getFloat(offset + positionOffset + Float.BYTES);
            float z = vertices.getFloat(offset + positionOffset + Float.BYTES * 2);

            minimum.x = Math.min(minimum.x, x);
            minimum.y = Math.min(minimum.y, y);
            minimum.z = Math.min(minimum.z, z);

            maximum.x = Math.max(maximum.x, x);
            maximum.y = Math.max(maximum.y, y);
            maximum.z = Math.max(maximum.z, z);
        }
    }
}