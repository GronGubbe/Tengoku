package net.grongubbe.tengoku.client.render.frame;

import net.grongubbe.tengoku.client.render.queue.RenderQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class RenderFrame {
    private final List<RenderView> views = new ArrayList<>();
    private final List<RenderLight> lights = new ArrayList<>();
    private final RenderQueue queue = new RenderQueue();

    public void addView(RenderView view) {
        views.add(Objects.requireNonNull(view, "view"));
    }

    public void addLight(RenderLight light) {
        lights.add(Objects.requireNonNull(light, "light"));
    }

    public void add(DrawCommand command) {
        queue.add(command);
    }

    public List<RenderView> views() {
        return Collections.unmodifiableList(views);
    }

    public List<RenderLight> lights() {
        return Collections.unmodifiableList(lights);
    }

    public List<DrawCommand> commands() {
        return queue.commands();
    }
}
