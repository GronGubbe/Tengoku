package net.grongubbe.tengoku.client.asset.model;

import java.util.List;

public final class Model {
    private final List<ModelPart> parts;

    public Model(List<ModelPart> parts) {
        this.parts = List.copyOf(parts);
    }

    public List<ModelPart> parts() {
        return parts;
    }
}