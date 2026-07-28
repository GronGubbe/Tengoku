package net.grongubbe.tengoku.client.asset.mesh;

import java.util.Objects;

public final class MeshData {
    private final VertexLayout layout;
    private final VertexBuffer vertices;
    private final IndexBuffer indices;

    public MeshData(VertexLayout layout, VertexBuffer vertices, IndexBuffer indices) {
        this.layout = Objects.requireNonNull(layout, "Mesh layout cannot be null");
        this.vertices = Objects.requireNonNull(vertices, "Mesh vertex buffer cannot be null");
        this.indices = Objects.requireNonNull(indices, "Mesh index buffer cannot be null");

        if (vertices.byteSize() == 0) {
            throw new IllegalArgumentException("Mesh cannot contain zero vertices");
        }

        if (indices.count() == 0) {
            throw new IllegalArgumentException("Mesh cannot contain zero indices");
        }
    }

    public VertexLayout layout() {
        return layout;
    }

    public VertexBuffer vertices() {
        return vertices;
    }

    public IndexBuffer indices() {
        return indices;
    }
}