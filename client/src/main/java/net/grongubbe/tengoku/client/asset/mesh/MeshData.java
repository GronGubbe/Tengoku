package net.grongubbe.tengoku.client.asset.mesh;

public final class MeshData {
    private final VertexLayout layout;
    private final VertexBuffer vertices;
    private final IndexBuffer indices;

    public MeshData(VertexLayout layout, VertexBuffer vertices, IndexBuffer indices) {
        this.layout = layout;
        this.vertices = vertices;
        this.indices = indices;
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