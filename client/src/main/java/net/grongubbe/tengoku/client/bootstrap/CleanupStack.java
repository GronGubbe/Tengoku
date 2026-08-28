package net.grongubbe.tengoku.client.bootstrap;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public final class CleanupStack implements AutoCloseable {
    private final Deque<CleanupEntry> entries = new ArrayDeque<>();

    private boolean closed;

    public void register(String name, AutoCloseable cleanup) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(cleanup, "cleanup");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Cleanup name must not be blank");
        }

        if (closed) {
            throw new IllegalStateException("Cleanup stack has already been closed");
        }

        entries.push(new CleanupEntry(name, cleanup));
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void clear() {
        entries.clear();
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        closed = true;

        RuntimeException failure = null;

        while (!entries.isEmpty()) {
            CleanupEntry entry = entries.pop();

            try {
                entry.cleanup().close();
            } catch (Exception exception) {
                RuntimeException cleanupFailure = new IllegalStateException("Failed to clean up: " + entry.name(), exception);

                if (failure == null) {
                    failure = new IllegalStateException("Cleanup failed");
                }

                failure.addSuppressed(cleanupFailure);
            }
        }

        if (failure != null) {
            throw failure;
        }
    }

    private record CleanupEntry(String name, AutoCloseable cleanup) {
    }
}