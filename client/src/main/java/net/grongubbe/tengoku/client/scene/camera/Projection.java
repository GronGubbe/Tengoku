package net.grongubbe.tengoku.client.scene.camera;

import org.joml.Matrix4f;

public interface Projection {
    Matrix4f matrix(Matrix4f destination);
}