package net.grongubbe.tengoku.client.render.scene;

import net.grongubbe.tengoku.client.asset.model.Model;

import java.util.Objects;

public final class RenderObject {
    private final Model model;
    private final Transform transform = new Transform();

    public RenderObject(Model model) {
        this.model = Objects.requireNonNull(model);
    }

    public Model model() {
        return model;
    }

    public Transform transform() {
        return transform;
    }
}