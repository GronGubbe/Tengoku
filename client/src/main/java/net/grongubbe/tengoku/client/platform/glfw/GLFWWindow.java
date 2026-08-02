package net.grongubbe.tengoku.client.platform.glfw;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class GLFWWindow implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(GLFWWindow.class);

    private long handle;

    private GLFWFramebufferSizeCallback framebufferSizeCallback;

    public GLFWWindow(int width, int height, String title, boolean fullscreen) {
        glfwDefaultWindowHints();

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        long monitor = fullscreen ? glfwGetPrimaryMonitor() : NULL;

        handle = glfwCreateWindow(width, height, title, monitor, NULL);

        if (handle == NULL) {
            throw new IllegalStateException("Failed to create GLFW window");
        }

        LOGGER.info("Created GLFW window {}x{}", width, height);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public void makeContextCurrent() {
        glfwMakeContextCurrent(handle);
    }

    public void show() {
        glfwShowWindow(handle);
    }

    public void swapBuffers() {
        glfwSwapBuffers(handle);
    }

    public void setSwapInterval(int interval) {
        glfwSwapInterval(interval);
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(handle, title);
    }

    public void setFramebufferSizeCallback(FramebufferSizeCallback callback) {
        if (framebufferSizeCallback != null) {
            framebufferSizeCallback.free();
        }

        framebufferSizeCallback = glfwSetFramebufferSizeCallback(handle, (_, width, height) ->
                callback.resized(width, height)
        );
    }

    @Override
    public void close() {
        if (handle == NULL) {
            return;
        }

        LOGGER.debug("Destroying GLFW window");

        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);

        handle = NULL;
    }
}