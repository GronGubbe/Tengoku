package net.grongubbe.tengoku.client.asset.serialization.model;

import java.util.List;

public final class ModelDefinition {
    private final List<ModelPartDefinition> parts;

    public ModelDefinition(List<ModelPartDefinition> parts) {
        this.parts = List.copyOf(parts);
    }

    public List<ModelPartDefinition> parts() {
        return parts;
    }
}