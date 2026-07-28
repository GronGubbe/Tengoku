package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.gpu.shader.ShaderUniforms;
import net.grongubbe.tengoku.client.render.RenderThread;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public final class OpenGLShaderUniforms implements ShaderUniforms {
    private final int model;
    private final int view;
    private final int projection;

    private final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public OpenGLShaderUniforms(int program) {
        this.model = glGetUniformLocation(program, "model");
        this.view = glGetUniformLocation(program, "view");
        this.projection = glGetUniformLocation(program, "projection");
    }

    @Override
    public void setModel(Matrix4f matrix) {
        upload(model, matrix);
    }

    @Override
    public void setView(Matrix4f matrix) {
        upload(view, matrix);
    }

    @Override
    public void setProjection(Matrix4f matrix) {
        upload(projection, matrix);
    }

    private void upload(int location, Matrix4f matrix) {
        RenderThread.assertCurrent();

        if (location == OpenGLUtils.invalidUniformLocation()) {
            return;
        }

        matrixBuffer.clear();
        matrix.get(matrixBuffer);

        glUniformMatrix4fv(location, false, matrixBuffer);
    }
}