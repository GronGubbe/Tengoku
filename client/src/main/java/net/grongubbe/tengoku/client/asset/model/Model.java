package net.grongubbe.tengoku.client.asset.model;

import net.grongubbe.tengoku.client.asset.Asset;

import java.util.List;

public final class Model implements Asset {
    private final ModelKey key;
    private final List<ModelPart> parts;

    public Model(ModelKey key, List<ModelPart> parts) {
        this.key = key;
        this.parts = List.copyOf(parts);
    }

    @Override
    public ModelKey key() {
        return key;
    }

    public List<ModelPart> parts() {
        return parts;
    }

    @Override
    public String toString() {
        return "Model[" + key.path() + "]";
    }
}