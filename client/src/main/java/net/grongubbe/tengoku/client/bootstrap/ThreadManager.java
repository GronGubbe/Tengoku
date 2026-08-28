package net.grongubbe.tengoku.client.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ThreadManager {
    private final List<ThreadEntry> entries = new ArrayList<>();

    private boolean started;
    private boolean shutdown;

    public void register(String name, Runnable task) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(task, "task");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Thread name must not be blank");
        }

        if (started) {
            throw new IllegalStateException("Thread manager has already started");
        }

        if (entries.stream().anyMatch(entry -> entry.name().equals(name))) {
            throw new IllegalArgumentException("Thread already registered: " + name);
        }

        Thread thread = Thread.ofPlatform().name(name).daemon(false).unstarted(task);

        entries.add(new ThreadEntry(name, thread));
    }

    public void start() {
        if (started) {
            throw new IllegalStateException("Thread manager has already started");
        }

        if (shutdown) {
            throw new IllegalStateException("Thread manager has already been shut down");
        }

        started = true;

        for (ThreadEntry entry : entries) {
            entry.thread().start();
        }
    }

    public void shutdown() {
        if (shutdown) {
            return;
        }

        shutdown = true;

        if (!started) {
            return;
        }

        for (ThreadEntry entry : entries) {
            entry.thread().interrupt();
        }

        boolean interrupted = false;

        for (ThreadEntry entry : entries) {
            while (entry.thread().isAlive()) {
                try {
                    entry.thread().join();
                } catch (InterruptedException exception) {
                    interrupted = true;
                }
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private record ThreadEntry(String name, Thread thread) {
    }
}