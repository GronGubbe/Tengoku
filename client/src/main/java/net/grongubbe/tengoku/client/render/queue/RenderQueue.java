package net.grongubbe.tengoku.client.render.queue;

import net.grongubbe.tengoku.client.render.frame.DrawCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class RenderQueue {
    private final static Logger LOGGER = LogManager.getLogger(RenderQueue.class);

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

        LOGGER.info("RenderQueue commands:\n{}", getLogMessage(commands));

        return List.copyOf(commands);
    }

    private String getLogMessage(List<DrawCommand> commands) {
        StringBuilder sb = new StringBuilder();
        for (DrawCommand command : commands) {
            sb.append(getCommandInfo(command)).append("\n");
        }
        return sb.toString();
    }

    private String getCommandInfo(DrawCommand command) {
        return String.format("Shader ID: %d, Material ID: %d, Mesh ID: %d",
                command.material().shader().id(),
                command.material().id(),
                command.mesh().id());
    }

    private void sort() {
        commands.sort(
                Comparator.comparingLong((DrawCommand command) -> command.material().shader().id())
                        .thenComparingLong(command -> command.material().id())
                        .thenComparingLong(command -> command.mesh().id())
        );
    }
}