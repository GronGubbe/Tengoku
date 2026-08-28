package net.grongubbe.tengoku.client.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class LifecycleCoordinator {
    private final List<LifecycleEntry> entries = new ArrayList<>();
    private final CleanupStack cleanupStack;

    private LifecycleState state = LifecycleState.NEW;

    public LifecycleCoordinator(CleanupStack cleanupStack) {
        this.cleanupStack = Objects.requireNonNull(cleanupStack, "cleanupStack");
    }

    public void register(String name, Lifecycle lifecycle) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(lifecycle, "lifecycle");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Lifecycle name must not be blank");
        }

        if (state != LifecycleState.NEW) {
            throw new IllegalStateException("Lifecycle coordinator has already started");
        }

        entries.add(new LifecycleEntry(name, lifecycle));
    }

    public LifecycleState state() {
        return state;
    }

    public void start() {
        if (state != LifecycleState.NEW) {
            throw new IllegalStateException("Lifecycle coordinator cannot start from state: " + state);
        }

        state = LifecycleState.STARTING;

        LifecycleEntry currentEntry = null;

        try {
            for (LifecycleEntry entry : entries) {
                cleanupStack.register(entry.name(), entry.lifecycle()::stop);

                currentEntry = entry;
                entry.lifecycle().start();

            }

            state = LifecycleState.RUNNING;
        } catch (RuntimeException exception) {
            state = LifecycleState.FAILED;

            String name = currentEntry == null ? "<none>" : currentEntry.name();

            RuntimeException failure = new IllegalStateException("Failed to start lifecycle component: " + name, exception);

            try {
                cleanupStack.close();
            } catch (RuntimeException cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }

            throw failure;
        }
    }

    public void stop() {
        if (state != LifecycleState.RUNNING) {
            throw new IllegalStateException("Lifecycle coordinator cannot stop from state: " + state);
        }

        state = LifecycleState.STOPPING;

        try {
            cleanupStack.close();
            state = LifecycleState.STOPPED;
        } catch (RuntimeException exception) {
            state = LifecycleState.FAILED;
            throw exception;
        }
    }

    private record LifecycleEntry(String name, Lifecycle lifecycle) {
    }
}