package net.grongubbe.tengoku.client.bootstrap;

import net.grongubbe.tengoku.client.render.Window;

import java.util.Objects;

public final class PlatformRuntime implements Lifecycle {
    private final int width;
    private final int height;
    private final String title;
    private final boolean fullscreen;
    private final boolean vsync;

    private Window window;

    public PlatformRuntime(int width, int height, String title, boolean fullscreen, boolean vsync) {
        if (width <= 0) {
            throw new IllegalArgumentException("Window width must be > 0");
        }

        if (height <= 0) {
            throw new IllegalArgumentException("Window height must be > 0");
        }

        Objects.requireNonNull(title, "title");

        if (title.isBlank()) {
            throw new IllegalArgumentException("Window title must not be blank");
        }

        this.width = width;
        this.height = height;
        this.title = title;
        this.fullscreen = fullscreen;
        this.vsync = vsync;
    }

    @Override
    public void start() {
        if (window != null) {
            throw new IllegalStateException("Platform runtime has already started");
        }

        window = new Window(width, height, title, fullscreen, vsync);
    }

    @Override
    public void stop() {
        if (window == null) {
            return;
        }

        window.close();
        window = null;
    }

    public Window window() {
        if (window == null) {
            throw new IllegalStateException("Platform runtime has not started");
        }

        return window;
    }
}