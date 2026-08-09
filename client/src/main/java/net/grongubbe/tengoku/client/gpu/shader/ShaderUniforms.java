package net.grongubbe.tengoku.client.gpu.shader;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface ShaderUniforms {
    void setModel(Matrix4f matrix);

    void setView(Matrix4f matrix);

    void setProjection(Matrix4f matrix);

    void setNormalMatrix(Matrix3f matrix);

    void setCameraPosition(Vector3f position);

    void setSunDirection(Vector3f direction);

    void setSunColor(Vector3f color);

    void setSunIntensity(float intensity);

    void setAmbientColor(Vector3f color);

    void setAmbientIntensity(float intensity);

    void setPointLightCount(int count);

    void setPointLightPosition(int index, Vector3f position);

    void setPointLightColor(int index, Vector3f color);

    void setPointLightRange(int index, float range);

    void setShadowMap(int textureUnit);

    void setShadowView(Matrix4f matrix);

    void setShadowProjection(Matrix4f matrix);
}