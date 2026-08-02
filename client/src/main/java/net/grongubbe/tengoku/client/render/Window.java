package net.grongubbe.tengoku.client.render;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.getCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public final class Window {
    private static final Logger LOGGER = LogManager.getLogger(Window.class);

    private final int width;
    private final int height;
    private final String title;
    private final boolean fullscreen;
    private final boolean vsync;

    private long window;

    public Window(int width, int height, String title, boolean fullscreen, boolean vsync) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.fullscreen = fullscreen;
        this.vsync = vsync;

        init();
    }

    private void init() {
        glfwDefaultWindowHints();

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        long monitor = fullscreen ? glfwGetPrimaryMonitor() : NULL;

        window = glfwCreateWindow(width, height, title, monitor, NULL);

        if (window == NULL) {
            throw new IllegalStateException("Failed to create GLFW window");
        }

        LOGGER.info("Created OpenGL window {}x{}", width, height);

        glfwMakeContextCurrent(window);
        createCapabilities();
        if (!getCapabilities().OpenGL33) {
            throw new IllegalStateException("OpenGL 3.3 or newer is required, detected " + glGetString(GL_VERSION));
        }

        LOGGER.info("OpenGL initialized: {}", glGetString(GL_VERSION));

        RenderThread.initialize();
        RenderThread.assertCurrent();

        glViewport(0, 0, width, height);

        glfwSwapInterval(vsync ? 1 : 0);

        glfwShowWindow(window);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void swapBuffers() {
        RenderThread.assertCurrent();

        glfwSwapBuffers(window);
    }
    
    public void pollEvents() {
        RenderThread.assertCurrent();

        glfwPollEvents();
    }
    
    public void setWindowTitle(String title) {
        RenderThread.assertCurrent();

        glfwSetWindowTitle(window, title);
    }

    public void dispose() {
        RenderThread.assertCurrent();

        LOGGER.debug("Destroying window");

        if (window != NULL) {
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
            window = NULL;
        }
    }
}
