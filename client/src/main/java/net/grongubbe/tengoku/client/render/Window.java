package net.grongubbe.tengoku.client.render;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_FOCUSED;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.getCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * TODO:
 *  Make cleaner split:
 *  client
 *  └── platform
 *       └── GLFWContext
 *            - glfwInit()
 *            - GLFWErrorCallback
 *            - glfwTerminate()
 *  render
 *  └── Window
 *       - glfwCreateWindow()
 *       - size
 *       - title
 *       - input callbacks
 */

public final class Window {
    private static final Logger LOGGER = LogManager.getLogger(Window.class);

    private final int width, height;
    private final String title;
    private final boolean fullscreen, vsync;

    private long window;
    private GLFWErrorCallback errorCallback;

    public Window(int width, int height, String title, boolean fullscreen, boolean vsync) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.fullscreen = fullscreen;
        this.vsync = vsync;

        init();
    }

    private void init() {
        errorCallback = GLFWErrorCallback.create((error, description) -> LOGGER.error("GLFW error {}: {}", error, MemoryUtil.memUTF8(description)));
        errorCallback.set();

        LOGGER.info("Initializing GLFW");

        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        
        long primaryMonitor = glfwGetPrimaryMonitor();
        window = glfwCreateWindow(width, height, title, fullscreen ? primaryMonitor : NULL, NULL);

        if (window == NULL) {
            cleanupInitFailure();

            throw new IllegalStateException("Failed to create GLFW window");
        }

        LOGGER.info("Created OpenGL window {}x{}", width, height);

        glfwMakeContextCurrent(window);

        try {
            createCapabilities();
        } catch (RuntimeException e) {
            cleanupInitFailure();
            throw new IllegalStateException("Failed to initialize OpenGL", e);
        }

        if (!getCapabilities().OpenGL33) {
            cleanupInitFailure();
            throw new IllegalStateException("OpenGL 3.3 or newer is required, detected " + glGetString(GL_VERSION));
        }

        LOGGER.info("OpenGL initialized: {}", glGetString(GL_VERSION));

        RenderThread.initialize();
        RenderThread.assertCurrent();

        glViewport(0, 0, width, height);
        
        glfwSwapInterval(vsync ? 1 : 0);
        glfwShowWindow(window);
    }

    private void cleanupInitFailure() {
        if (window != NULL) {
            glfwDestroyWindow(window);
            window = NULL;
        }

        glfwTerminate();

        if (errorCallback != null) {
            errorCallback.free();
            errorCallback = null;
        }
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
        LOGGER.trace("Swapping buffers");
        glfwSwapBuffers(window);
    }
    
    public void pollEvents() {
        RenderThread.assertCurrent();
        LOGGER.trace("Polling events");
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

        glfwTerminate();

        if (errorCallback != null) {
            errorCallback.free();
            errorCallback = null;
        }
    }
}
