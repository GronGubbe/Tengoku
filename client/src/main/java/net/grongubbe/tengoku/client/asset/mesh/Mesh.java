package net.grongubbe.tengoku.client.asset.mesh;

import java.util.List;

public final class Mesh {
    private final MeshData data;
    private final List<SubMesh> subMeshes;

    public Mesh(MeshData data, List<SubMesh> subMeshes) {
        this.data = data;
        this.subMeshes = List.copyOf(subMeshes);
    }

    public MeshData data() {
        return data;
    }

    public List<SubMesh> subMeshes() {
        return subMeshes;
    }
}