package net.grongubbe.tengoku.client.graphics.shader;

import static org.lwjgl.opengl.GL20.*;

public final class ShaderStage {
    private final int id;

    public ShaderStage(int type, String source) {
        this.id = glCreateShader(type);

        glShaderSource(id, source);
        glCompileShader(id);

        if (glGetShaderi(id, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException("Shader compiling error: " + glGetShaderInfoLog(id));
        }
    }

    public int getId() {
        return id;
    }
}