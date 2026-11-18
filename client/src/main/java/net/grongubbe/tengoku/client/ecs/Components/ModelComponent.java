package net.grongubbe.tengoku.client.ecs.Components;

import net.grongubbe.tengoku.client.asset.assets.ModelAsset;
import net.grongubbe.tengoku.client.ecs.IComponent;

import java.util.Objects;

public final class ModelComponent implements IComponent {
    private final ModelAsset modelAsset;

    public ModelComponent(ModelAsset modelAsset) {
        this.modelAsset = Objects.requireNonNull(modelAsset, "modelAsset");
    }

    public ModelAsset getModelAsset() {
        return modelAsset;
    }
}