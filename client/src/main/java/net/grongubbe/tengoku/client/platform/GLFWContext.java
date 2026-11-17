package net.grongubbe.tengoku.client.platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

public final class GLFWContext implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(GLFWContext.class);

    private final GLFWErrorCallback errorCallback;

    private boolean initialized;

    public GLFWContext() {
        errorCallback = GLFWErrorCallback.create((error, description) ->
                LOGGER.error("GLFW Error [0x{}]: {}", Integer.toHexString(error), MemoryUtil.memUTF8(description))
        );

        try {
            errorCallback.set();

            LOGGER.debug("Initializing GLFW");

            if (!GLFW.glfwInit()) {
                throw new IllegalStateException("Unable to initialize GLFW");
            }

            initialized = true;

        } catch (RuntimeException | Error e) {
            try {
                GLFW.glfwSetErrorCallback(null);
            } finally {
                errorCallback.free();
            }

            throw e;
        }
    }

    public void pollEvents() {
        if (!initialized) {
            throw new IllegalStateException("GLFW is not initialized");
        }

        GLFW.glfwPollEvents();
    }

    @Override
    public void close() {
        if (!initialized) {
            return;
        }

        initialized = false;

        LOGGER.debug("Terminating GLFW");

        try {
            GLFW.glfwTerminate();
        } finally {
            try {
                GLFW.glfwSetErrorCallback(null);
            } finally {
                errorCallback.free();
            }
        }
    }
}