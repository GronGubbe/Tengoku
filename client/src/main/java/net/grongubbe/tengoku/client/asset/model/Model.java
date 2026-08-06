package net.grongubbe.tengoku.client.asset.model;

import net.grongubbe.tengoku.client.asset.Asset;
import net.grongubbe.tengoku.client.asset.bounds.BoundingVolume;

import java.util.List;

public final class Model implements Asset {
    private final ModelKey key;
    private final List<ModelPart> parts;
    private final BoundingVolume bounds;

    public Model(ModelKey key, List<ModelPart> parts, BoundingVolume bounds) {
        this.key = key;
        this.parts = List.copyOf(parts);
        this.bounds = bounds;
    }

    @Override
    public ModelKey key() {
        return key;
    }

    public List<ModelPart> parts() {
        return parts;
    }

    public BoundingVolume bounds() {
        return bounds;
    }

    @Override
    public String toString() {
        return "Model[" + key.path() + "]";
    }
}