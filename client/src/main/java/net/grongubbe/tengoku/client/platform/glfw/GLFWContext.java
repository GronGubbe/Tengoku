package net.grongubbe.tengoku.client.platform.glfw;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public final class GLFWContext implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(GLFWContext.class);

    private final GLFWErrorCallback errorCallback;

    private boolean closed;

    public GLFWContext() {
        errorCallback = GLFWErrorCallback.create((error, description) ->
                LOGGER.error("GLFW error {}: {}", error, MemoryUtil.memUTF8(description))
        );

        errorCallback.set();

        LOGGER.info("Initializing GLFW");

        if (!glfwInit()) {
            errorCallback.free();
            throw new IllegalStateException("Failed to initialize GLFW");
        }
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        LOGGER.debug("Terminating GLFW");

        glfwTerminate();

        errorCallback.free();

        closed = true;
    }
}