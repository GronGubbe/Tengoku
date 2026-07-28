package net.grongubbe.tengoku.client.gpu.shader;

import org.joml.Matrix4f;

public interface ShaderUniforms {
    void setModel(Matrix4f matrix);
    void setView(Matrix4f matrix);
    void setProjection(Matrix4f matrix);
}