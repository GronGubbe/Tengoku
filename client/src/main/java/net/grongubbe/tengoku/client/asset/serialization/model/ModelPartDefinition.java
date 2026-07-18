package net.grongubbe.tengoku.client.asset.serialization.model;

import java.nio.file.Path;
import java.util.List;

public final class ModelPartDefinition {
    private final Path mesh;
    private final List<Path> materials;

    public ModelPartDefinition(Path mesh, List<Path> materials) {
        this.mesh = mesh;
        this.materials = List.copyOf(materials);
    }

    public Path mesh() {
        return mesh;
    }

    public List<Path> materials() {
        return materials;
    }
}