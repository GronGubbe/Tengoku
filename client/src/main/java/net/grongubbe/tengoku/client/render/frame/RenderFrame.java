package net.grongubbe.tengoku.client.render.frame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RenderFrame {
    private final List<RenderView> views = new ArrayList<>();
    private final List<DrawCommand> commands = new ArrayList<>();

    public void addView(RenderView view) {
        views.add(view);
    }

    public void add(DrawCommand command) {
        commands.add(command);
    }

    public List<RenderView> views() {
        return Collections.unmodifiableList(views);
    }

    public List<DrawCommand> commands() {
        return Collections.unmodifiableList(commands);
    }

    public void clear() {
        views.clear();
        commands.clear();
    }
}