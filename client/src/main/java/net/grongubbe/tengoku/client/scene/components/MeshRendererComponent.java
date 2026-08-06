package net.grongubbe.tengoku.client.scene.components;

import net.grongubbe.tengoku.client.asset.model.Model;
import net.grongubbe.tengoku.client.scene.Component;

import java.util.Objects;

public final class MeshRendererComponent implements Component {
    private final Model model;

    public MeshRendererComponent(Model model) {
        this.model = Objects.requireNonNull(model, "model");
    }

    public Model model() {
        return model;
    }
}