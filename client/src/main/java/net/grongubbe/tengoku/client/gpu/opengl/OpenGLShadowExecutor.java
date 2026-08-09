package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.gpu.shader.GpuShader;
import net.grongubbe.tengoku.client.render.RenderThread;
import net.grongubbe.tengoku.client.render.frame.DrawCommand;
import net.grongubbe.tengoku.client.render.frame.ShadowView;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public final class OpenGLShadowExecutor {
    private static final FloatBuffer TEMP_MATRIX_BUFFER = BufferUtils.createFloatBuffer(16);

    private final OpenGLMeshBinder meshBinder;

    private final int program;
    private final int model;
    private final int lightView;
    private final int lightProjection;

    private final Matrix4f modelMatrix = new Matrix4f();
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f();

    public OpenGLShadowExecutor(GpuShader shader, OpenGLMeshBinder meshBinder) {
        RenderThread.assertCurrent();

        this.meshBinder = Objects.requireNonNull(meshBinder, "meshBinder");

        GpuShader gpuShader = Objects.requireNonNull(shader, "shader");
        this.program = gpuShader.program();

        this.model = requireUniform(program, "model");
        this.lightView = requireUniform(program, "lightView");
        this.lightProjection = requireUniform(program, "lightProjection");
    }

    public void begin() {
        RenderThread.assertCurrent();

        glUseProgram(program);
    }

    public void draw(DrawCommand command, ShadowView shadowView) {
        RenderThread.assertCurrent();

        command.modelMatrix(modelMatrix);

        shadowView.view(viewMatrix);
        shadowView.projection(projectionMatrix);

        upload(model, modelMatrix);
        upload(lightView, viewMatrix);
        upload(lightProjection, projectionMatrix);

        meshBinder.bind(command.mesh());

        try {
            glDrawElements(
                    GL_TRIANGLES,
                    command.indexCount(),
                    GL_UNSIGNED_INT,
                    (long) command.indexOffset() * Integer.BYTES
            );
        } finally {
            meshBinder.unbind();
        }
    }

    public void end() {
        RenderThread.assertCurrent();

        glUseProgram(0);
    }

    private static int requireUniform(int program, String name) {
        int location = glGetUniformLocation(program, name);

        if (location == -1) {
            throw new IllegalStateException("Shadow shader is missing required uniform: " + name);
        }

        return location;
    }

    private static void upload(int location, Matrix4f matrix) {
        TEMP_MATRIX_BUFFER.clear();
        matrix.get(TEMP_MATRIX_BUFFER);

        glUniformMatrix4fv(location, false, TEMP_MATRIX_BUFFER);
    }
}