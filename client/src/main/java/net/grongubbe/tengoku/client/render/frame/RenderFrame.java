package net.grongubbe.tengoku.client.render.frame;

import net.grongubbe.tengoku.client.render.queue.RenderQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RenderFrame {
    private final List<RenderView> views = new ArrayList<>();
    private final RenderQueue queue = new RenderQueue();

    public void addView(RenderView view) {
        views.add(view);
    }

    public void add(DrawCommand command) {
        queue.add(command);
    }

    public List<RenderView> views() {
        return Collections.unmodifiableList(views);
    }

    public List<DrawCommand> commands() {
        return queue.commands();
    }
}