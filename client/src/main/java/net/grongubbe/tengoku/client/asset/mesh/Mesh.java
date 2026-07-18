package net.grongubbe.tengoku.client.asset.mesh;

import java.util.List;

public final class Mesh {
    private final MeshData data;
    private final List<MeshSection> meshSections;

    public Mesh(MeshData data, List<MeshSection> meshSections) {
        this.data = data;
        this.meshSections = List.copyOf(meshSections);
    }

    public MeshData data() {
        return data;
    }

    public List<MeshSection> subMeshes() {
        return meshSections;
    }
}