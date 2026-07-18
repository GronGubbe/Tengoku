package net.grongubbe.tengoku.client.render;

public final class RenderThread {
    private static Thread thread;

    private RenderThread() {
    }

    public static void initialize() {
        thread = Thread.currentThread();
    }

    public static boolean isCurrent() {
        return Thread.currentThread() == thread;
    }

    public static void assertCurrent() {
        if (!isCurrent()) {
            throw new IllegalStateException("OpenGL operation must run on the render thread.");
        }
    }
}