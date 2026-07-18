package net.grongubbe.tengoku.client.asset.serialization.mesh;

import java.nio.file.Path;

public final class MeshDefinition {
    private final Path model;

    public MeshDefinition(Path model) {
        this.model = model;
    }

    public Path model() {
        return model;
    }
}