package net.grongubbe.tengoku.client.render.frame;

import net.grongubbe.tengoku.client.gpu.material.GpuMaterial;
import net.grongubbe.tengoku.client.gpu.mesh.GpuMesh;
import org.joml.Matrix4f;

import java.util.Objects;

public final class DrawCommand {
    private final Matrix4f modelMatrix;

    private final GpuMesh mesh;
    private final GpuMaterial material;

    private final int indexOffset;
    private final int indexCount;

    public DrawCommand(Matrix4f modelMatrix, GpuMesh mesh, GpuMaterial material, int indexOffset, int indexCount) {
        this.modelMatrix = new Matrix4f(Objects.requireNonNull(modelMatrix, "modelMatrix"));
        this.mesh = Objects.requireNonNull(mesh, "mesh");
        this.material = Objects.requireNonNull(material, "material");

        if (indexOffset < 0) {
            throw new IllegalArgumentException("DrawCommand indexOffset must be >= 0");
        }

        if (indexCount <= 0) {
            throw new IllegalArgumentException("DrawCommand indexCount must be > 0");
        }

        this.indexOffset = indexOffset;
        this.indexCount = indexCount;
    }

    public Matrix4f modelMatrix(Matrix4f destination) {
        Objects.requireNonNull(destination, "destination");
        return destination.set(modelMatrix);
    }

    public GpuMesh mesh() {
        return mesh;
    }

    public GpuMaterial material() {
        return material;
    }

    public int indexOffset() {
        return indexOffset;
    }

    public int indexCount() {
        return indexCount;
    }
}