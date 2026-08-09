package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.gpu.shader.ShaderUniforms;
import net.grongubbe.tengoku.client.render.RenderThread;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static net.grongubbe.tengoku.client.render.RenderingConstants.MAX_POINT_LIGHTS;
import static org.lwjgl.opengl.GL20.*;

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

    private final int pointLightCount;

    private final int[] pointLightPositions;
    private final int[] pointLightColors;
    private final int[] pointLightRanges;

    private final int shadowMap;
    private final int shadowView;
    private final int shadowProjection;

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

        pointLightCount = glGetUniformLocation(program, "pointLightCount");

        pointLightPositions = new int[MAX_POINT_LIGHTS];
        pointLightColors = new int[MAX_POINT_LIGHTS];
        pointLightRanges = new int[MAX_POINT_LIGHTS];

        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            pointLightPositions[i] = glGetUniformLocation(program, "pointLightPositions[" + i + "]");
            pointLightColors[i] = glGetUniformLocation(program, "pointLightColors[" + i + "]");
            pointLightRanges[i] = glGetUniformLocation(program, "pointLightRanges[" + i + "]");
        }

        shadowMap = glGetUniformLocation(program, "shadowMap");
        shadowView = glGetUniformLocation(program, "shadowView");
        shadowProjection = glGetUniformLocation(program, "shadowProjection");
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

    @Override
    public void setPointLightCount(int count) {
        if (count < 0 || count > MAX_POINT_LIGHTS) {
            throw new IllegalArgumentException("Point light count must be between 0 and " + MAX_POINT_LIGHTS);
        }

        upload(pointLightCount, count);
    }

    @Override
    public void setPointLightPosition(int index, Vector3f position) {
        validatePointLightIndex(index);
        upload(pointLightPositions[index], position);
    }

    @Override
    public void setPointLightColor(int index, Vector3f color) {
        validatePointLightIndex(index);
        upload(pointLightColors[index], color);
    }

    @Override
    public void setPointLightRange(int index, float range) {
        validatePointLightIndex(index);
        upload(pointLightRanges[index], range);
    }

    @Override public void setShadowMap(int textureUnit) {
        upload(shadowMap, textureUnit);
    }

    @Override public void setShadowView(Matrix4f matrix) {
        upload(shadowView, matrix);
    }

    @Override public void setShadowProjection(Matrix4f matrix) {
        upload(shadowProjection, matrix);
    }

    private void validatePointLightIndex(int index) {
        if (index < 0 || index >= MAX_POINT_LIGHTS) {
            throw new IllegalArgumentException("Point light index must be between 0 and " + (MAX_POINT_LIGHTS - 1));
        }
    }

    private void upload(int location, int value) {
        RenderThread.assertCurrent();

        if (location == OpenGLUtils.invalidUniformLocation()) {
            return;
        }

        glUniform1i(location, value);
    }

    private void upload(int location, float value) {
        RenderThread.assertCurrent();

        if (location == OpenGLUtils.invalidUniformLocation()) {
            return;
        }

        glUniform1f(location, value);
    }

    private void upload(int location, Vector3f vector) {
        RenderThread.assertCurrent();

        if (location == OpenGLUtils.invalidUniformLocation()) {
            return;
        }

        glUniform3f(location, vector.x, vector.y, vector.z);
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