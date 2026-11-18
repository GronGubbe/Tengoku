package net.grongubbe.tengoku.client.render;

import org.joml.Matrix4f;

public record Camera(Matrix4f viewMatrix, Matrix4f projectionMatrix) {
}
