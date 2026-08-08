package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.gpu.shader.ShaderUniforms;
import net.grongubbe.tengoku.client.render.RenderThread;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public final class OpenGLShaderUniforms implements ShaderUniforms {
    private final int model;
    private final int view;
    private final int projection;
    private final int normalMatrix;

    private final int cameraPosition;

    private final int sunDirection;
    private final int sunColor;
    private final int sunIntensity;

    private final int ambientColor;
    private final int ambientIntensity;

    private final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer normalMatrixBuffer = BufferUtils.createFloatBuffer(9);

    public OpenGLShaderUniforms(int program) {
        model = glGetUniformLocation(program, "model");
        view = glGetUniformLocation(program, "view");
        projection = glGetUniformLocation(program, "projection");
        normalMatrix = glGetUniformLocation(program, "normalMatrix");

        cameraPosition = glGetUniformLocation(program, "cameraPosition");

        sunDirection = glGetUniformLocation(program, "sunDirection");
        sunColor = glGetUniformLocation(program, "sunColor");
        sunIntensity = glGetUniformLocation(program, "sunIntensity");

        ambientColor = glGetUniformLocation(program, "ambientColor");
        ambientIntensity = glGetUniformLocation(program, "ambientIntensity");
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

    @Override
    public void setNormalMatrix(Matrix3f matrix) {
        RenderThread.assertCurrent();

        if (normalMatrix == OpenGLUtils.invalidUniformLocation()) {
            return;
        }

        normalMatrixBuffer.clear();
        matrix.get(normalMatrixBuffer);

        glUniformMatrix3fv(normalMatrix, false, normalMatrixBuffer);
    }

    @Override
    public void setCameraPosition(Vector3f position) {
        upload(cameraPosition, position);
    }

    @Override
    public void setSunDirection(Vector3f direction) {
        upload(sunDirection, direction);
    }

    @Override
    public void setSunColor(Vector3f color) {
        upload(sunColor, color);
    }

    @Override
    public void setSunIntensity(float intensity) {
        upload(sunIntensity, intensity);
    }

    @Override
    public void setAmbientColor(Vector3f color) {
        upload(ambientColor, color);
    }

    @Override
    public void setAmbientIntensity(float intensity) {
        upload(ambientIntensity, intensity);
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

    private void upload(int location, Vector3f vector) {
        RenderThread.assertCurrent();

        if (location == OpenGLUtils.invalidUniformLocation()) {
            return;
        }

        glUniform3f(location, vector.x, vector.y, vector.z);
    }

    private void upload(int location, float value) {
        RenderThread.assertCurrent();

        if (location == OpenGLUtils.invalidUniformLocation()) {
            return;
        }

        glUniform1f(location, value);
    }
}