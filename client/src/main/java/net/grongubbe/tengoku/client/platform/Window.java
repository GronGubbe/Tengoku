package net.grongubbe.tengoku.client.platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public final class Window implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(Window.class);

    private GLFWFramebufferSizeCallbackI framebufferSizeCallback;

    private final long handle;

    private boolean closed;

    public Window(String title, int width, int height, boolean vsync) {
        LOGGER.debug("Creating new window: Title:{}, Width:{}, Height:{}, Vsync:{}", title, width, height, vsync);

        GLFW.glfwDefaultWindowHints();

        this.handle = GLFW.glfwCreateWindow(width, height, title, 0L, 0L);

        if (handle == 0L) {
            throw new IllegalStateException("Failed to create GLFW window");
        }

        makeContextCurrent();
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }

    public long getHandle() {
        return handle;
    }

    public int getFramebufferWidth() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);

            GLFW.glfwGetFramebufferSize(handle, width, height);

            return width.get(0);
        }
    }

    public int getFramebufferHeight() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);

            GLFW.glfwGetFramebufferSize(handle, width, height);

            return height.get(0);
        }
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(handle);
    }

    public void makeContextCurrent() {
        GLFW.glfwMakeContextCurrent(handle);
    }

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(handle);
    }

    public void setFramebufferSizeCallback(GLFWFramebufferSizeCallbackI callback) {
        framebufferSizeCallback = callback;
        GLFW.glfwSetFramebufferSizeCallback(handle, callback);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        closed = true;

        LOGGER.debug("Closing window");

        GLFW.glfwSetFramebufferSizeCallback(handle, null);

        GLFW.glfwDestroyWindow(handle);
    }
}