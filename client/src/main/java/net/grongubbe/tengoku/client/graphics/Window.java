package net.grongubbe.tengoku.client.graphics;

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
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    
    private final int width, height;
    private final String title;
    private final boolean fullscreen, vsync;

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
            dispose();
            throw new IllegalStateException("Failed to create GLFW window");
        }
        
        glfwMakeContextCurrent(window);
        createCapabilities();
        glViewport(0, 0, width, height);
        
        glfwSwapInterval(vsync ? 1 : 0);
        glfwShowWindow(window);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }
    
    public void swapBuffers() {
        glfwSwapBuffers(window);
    }
    
    public void poolEvents() {
        glfwPollEvents();
    }
    
    public void setWindowTitle(String title) {
        glfwSetWindowTitle(window, title);
    }

    public void dispose() {
        if (window != NULL) {
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
        }
        
        glfwTerminate();
    }
}