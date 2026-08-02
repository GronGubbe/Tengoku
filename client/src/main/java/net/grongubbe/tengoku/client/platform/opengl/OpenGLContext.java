package net.grongubbe.tengoku.client.platform.opengl;

import net.grongubbe.tengoku.client.platform.glfw.GLFWWindow;
import net.grongubbe.tengoku.client.render.RenderThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.getCapabilities;
import static org.lwjgl.opengl.GL11.*;

public final class OpenGLContext {
    private static final Logger LOGGER = LogManager.getLogger(OpenGLContext.class);

    public OpenGLContext(GLFWWindow window, int width, int height) {
        window.makeContextCurrent();

        createCapabilities();

        if (!getCapabilities().OpenGL33) {
            throw new IllegalStateException("OpenGL 3.3 or newer is required, detected " + glGetString(GL_VERSION));
        }

        LOGGER.info("OpenGL initialized: {}", glGetString(GL_VERSION));

        RenderThread.initialize();
        RenderThread.assertCurrent();

        initialize();
        resize(width, height);
    }

    private void initialize() {
        glEnable(GL_DEPTH_TEST);
    }

    public void resize(int width, int height) {
        glViewport(0, 0, width, height);
    }
}