package net.grongubbe.tengoku.client.graphics.shader;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader {
    private final int id;

    protected Shader(ShaderStage... stages) {
        this.id = glCreateProgram();

        for (ShaderStage stage : stages) {
            glAttachShader(id, stage.getId());
        }

        glLinkProgram(id);

        if (glGetProgrami(id, GL_LINK_STATUS) == 0) {
            throw new RuntimeException("Shader linking error: " + glGetProgramInfoLog(id));
        }

        for (ShaderStage stage : stages) {
            glDetachShader(id, stage.getId());
        }
    }

    public void bind() {
        glUseProgram(id);
    }

    public void unbind() {
        glUseProgram(0);
    }
}
