package net.grongubbe.tengoku.client.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RenderScene {
    private final List<RenderObject> objects = new ArrayList<>();

    public void add(RenderObject object) {
        objects.add(Objects.requireNonNull(object));
    }

    public List<RenderObject> objects() {
        return List.copyOf(objects);
    }
}