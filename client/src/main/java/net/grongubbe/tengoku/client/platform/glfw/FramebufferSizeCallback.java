package net.grongubbe.tengoku.client.platform.glfw;

@FunctionalInterface
public interface FramebufferSizeCallback {
    void resized(int width, int height);
}