package net.grongubbe.tengoku.client.gpu.opengl;

import static org.lwjgl.opengl.GL30.*;

public class OpenGLUtils {
    private static final int INVALID_LOCATION = -1;

    private OpenGLUtils() {
    }

    public static int invalidUniformLocation() {
        return INVALID_LOCATION;
    }

    public static String errorName(int error) {
        return switch (error) {
            case GL_NO_ERROR -> "GL_NO_ERROR (0x%04X)".formatted(error);
            case GL_INVALID_ENUM -> "GL_INVALID_ENUM (0x%04X)".formatted(error);
            case GL_INVALID_VALUE -> "GL_INVALID_VALUE (0x%04X)".formatted(error);
            case GL_INVALID_OPERATION -> "GL_INVALID_OPERATION (0x%04X)".formatted(error);
            case GL_INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION (0x%04X)".formatted(error);
            case GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY (0x%04X)".formatted(error);
            default -> "Unknown (0x%04X)".formatted(error);
        };
    }
}
