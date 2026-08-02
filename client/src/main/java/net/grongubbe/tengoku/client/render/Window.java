package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.platform.glfw.GLFWContext;
import net.grongubbe.tengoku.client.platform.glfw.GLFWWindow;
import net.grongubbe.tengoku.client.platform.opengl.OpenGLContext;
import net.grongubbe.tengoku.client.scene.camera.Camera;

public final class Window implements AutoCloseable {
    private int width;
    private int height;
    private boolean closed;

    private final GLFWContext glfwContext;
    private final GLFWWindow glfwWindow;
    private final OpenGLContext openGLContext;

    private Camera resizeCamera;

    public Window(int width, int height, String title, boolean fullscreen, boolean vsync) {
        this.width = width;
        this.height = height;

        GLFWContext createdContext = null;
        GLFWWindow createdWindow = null;
        OpenGLContext createdOpenGLContext;

        try {
            createdContext = new GLFWContext();
            createdWindow = new GLFWWindow(width, height, title, fullscreen);

            createdOpenGLContext = new OpenGLContext(createdWindow, width, height);

            createdWindow.setFramebufferSizeCallback(this::resize);
            createdWindow.setSwapInterval(vsync ? 1 : 0);
            createdWindow.show();

            glfwContext = createdContext;
            glfwWindow = createdWindow;
            openGLContext = createdOpenGLContext;

        } catch (Throwable throwable) {
            if (createdWindow != null) {
                createdWindow.close();
            }

            if (createdContext != null) {
                createdContext.close();
            }

            throw throwable;
        }
    }

    public void setResizeCamera(Camera camera) {
        this.resizeCamera = camera;

        camera.resize(width, height);
    }

    private void resize(int width, int height) {
        this.width = width;
        this.height = height;

        openGLContext.resize(width, height);

        if (resizeCamera != null) {
            resizeCamera.resize(width, height);
        }
    }

    public boolean shouldClose() {
        return glfwWindow.shouldClose();
    }

    public int framebufferWidth() {
        return width;
    }

    public int framebufferHeight() {
        return height;
    }

    public void swapBuffers() {
        RenderThread.assertCurrent();

        glfwWindow.swapBuffers();
    }
    
    public void pollEvents() {
        RenderThread.assertCurrent();

        glfwWindow.pollEvents();
    }
    
    public void setWindowTitle(String title) {
        RenderThread.assertCurrent();

        glfwWindow.setTitle(title);
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        closed = true;

        RenderThread.assertCurrent();

        try {
            glfwWindow.close();
        } finally {
            glfwContext.close();
        }
    }
}
