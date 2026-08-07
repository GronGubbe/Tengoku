package net.grongubbe.tengoku.client.render.queue;

import net.grongubbe.tengoku.client.render.frame.DrawCommand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class RenderQueue {
    private final List<DrawCommand> commands = new ArrayList<>();

    private boolean sorted;

    public void add(DrawCommand command) {
        commands.add(Objects.requireNonNull(command, "command"));
        sorted = false;
    }

    public List<DrawCommand> commands() {
        if (!sorted) {
            sort();
            sorted = true;
        }

        return List.copyOf(commands);
    }

    private void sort() {
        commands.sort(
                Comparator.comparingLong((DrawCommand command) -> command.material().shader().id())
                        .thenComparingLong(command -> command.material().id())
                        .thenComparingLong(command -> command.mesh().id())
        );
    }
}