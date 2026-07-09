package net.grongubbe.tengoku.client.graphics.shader;

import net.grongubbe.tengoku.client.graphics.material.Material;
import net.grongubbe.tengoku.client.graphics.material.MaterialType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader {
    private static final Logger LOGGER = LogManager.getLogger(Shader.class);
    protected final int id;

    protected final Handler[] handlers = new Handler[MaterialType.values().length];

    protected Shader(ShaderStage... stages) {
        try {
            this.id = glCreateProgram();

            if (id == 0) {
                throw new IllegalStateException("Failed to create shader program");
            }

            for (ShaderStage stage : stages) {
                glAttachShader(id, stage.getId());
            }

            glLinkProgram(id);

            if (glGetProgrami(id, GL_LINK_STATUS) == 0) {
                StringBuilder sb = new StringBuilder();

                sb.append("Failed to link shader program\n");
                sb.append("Attached stages:\n");

                for (int i = 0; i < stages.length; i++) {
                    ShaderStage stage = stages[i];
                    sb.append("  - File: ").append(stage.getPath()).append('\n');
                    sb.append("    Type: ").append(stage.getTypeName()).append('\n');
                    if (i != stages.length - 1) {
                        sb.append("\n");
                    }
                }

                sb.append("\nOpenGL Log:\n");
                sb.append(glGetProgramInfoLog(id));

                String error = sb.toString();
                LOGGER.error("Shader linking failed:\n{}", error);
                throw new IllegalStateException(error);
            }
            
            LOGGER.info("Shader program created and linked successfully [id={}]", id);
        } finally {
            cleanUpStages(stages);
        }
    }

    public abstract void setUniforms(Material material);

    public abstract void loadUniformLocations();

    protected void bind(MaterialType type, Handler handler) {
        handlers[type.ordinal()] = handler;
    }

    public void setVec3(int loc, Vector3f value) {
        glUniform3f(loc, value.x, value.y, value.z);
    }

    public void setFloat(int loc, float value) {
        glUniform1f(loc, value);
    }

    public void bind() {
        glUseProgram(id);
    }

    private void cleanUpStages(ShaderStage... stages) {
        for (ShaderStage stage : stages) {
            glDetachShader(id, stage.getId());
            stage.delete();
        }
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void delete() {
        glDeleteProgram(id);
    }

    @FunctionalInterface
    protected interface Handler {
        void apply(Material material);
    }
}
