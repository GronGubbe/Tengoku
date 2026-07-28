package net.grongubbe.tengoku.client.asset.mesh;

import net.grongubbe.tengoku.client.asset.Asset;

import java.util.List;
import java.util.Objects;

public final class Mesh implements Asset {
    private final MeshKey key;
    private final MeshData data;
    private final List<MeshSection> meshSections;

    public Mesh(MeshKey key, MeshData data, List<MeshSection> meshSections) {
        this.key = Objects.requireNonNull(key, "Mesh key cannot be null");
        this.data = Objects.requireNonNull(data, "Mesh data cannot be null");

        Objects.requireNonNull(meshSections, "Mesh sections cannot be null");

        if (meshSections.isEmpty()) {
            throw new IllegalArgumentException("Mesh must contain at least one section");
        }

        this.meshSections = List.copyOf(meshSections);

        validateSections();
    }

    private void validateSections() {
        int indexCount = data.indices().count();

        for (MeshSection section : meshSections) {
            int end = section.indexOffset() + section.indexCount();

            if (end > indexCount) {
                throw new IllegalArgumentException("""
                        Mesh section exceeds index buffer bounds.

                        Index buffer size:
                        %d

                        Section:
                        offset=%d
                        count=%d
                        """.formatted(indexCount, section.indexOffset(), section.indexCount())
                );
            }
        }
    }

    @Override
    public MeshKey key() {
        return key;
    }

    public MeshData data() {
        return data;
    }

    public List<MeshSection> subMeshes() {
        return meshSections;
    }

    @Override
    public String toString() {
        return "Mesh[" + key.path() + "]";
    }
}