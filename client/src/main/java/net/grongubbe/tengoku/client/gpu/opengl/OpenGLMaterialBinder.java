package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.asset.shader.MaterialParameterDefinition;
import net.grongubbe.tengoku.client.gpu.material.GpuMaterial;
import net.grongubbe.tengoku.client.gpu.texture.GpuTexture;
import net.grongubbe.tengoku.client.render.RenderThread;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL20.*;

public final class OpenGLMaterialBinder {
    public void bind(GpuMaterial material) {
        RenderThread.assertCurrent();

        glUseProgram(material.shader().program());

        for (MaterialParameterDefinition parameter : material.layout().parameters()) {
            Object value = material.values().get(parameter.slot());

            if (value == null) {
                continue;
            }

            int location = material.uniformLocation(parameter.slot());

            if (location == -1) {
                continue;
            }

            upload(location, value);
        }
    }

    private void upload(int location, Object value) {
        switch (value) {
            case Float number -> glUniform1f(location, number);
            case Vector2f vector -> glUniform2f(location, vector.x, vector.y);
            case Vector3f vector -> glUniform3f(location, vector.x, vector.y, vector.z);
            case Vector4f vector -> glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
            case GpuTexture texture -> uploadTexture(location, texture);
            default -> throw new IllegalArgumentException("""
                                Unsupported GPU material value.
                        
                                Type:
                                %s
                                """.formatted(value.getClass().getName())
            );
        }
    }

    private void uploadTexture(int location, GpuTexture texture) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.id());
        glUniform1i(location, 0);
    }
}