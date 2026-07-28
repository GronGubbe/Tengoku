package net.grongubbe.tengoku.client.asset.mesh.importer;

import net.grongubbe.tengoku.client.asset.mesh.MeshData;
import net.grongubbe.tengoku.client.asset.mesh.MeshSection;

import java.util.List;

public final class ImportedMesh {
    private final MeshData data;
    private final List<MeshSection> sections;

    public ImportedMesh(MeshData data, List<MeshSection> sections) {
        this.data = data;
        this.sections = List.copyOf(sections);
    }

    public MeshData data() {
        return data;
    }

    public List<MeshSection> sections() {
        return sections;
    }
}