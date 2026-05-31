package net.grongubbe.tengoku.client.graphics;

import net.grongubbe.tengoku.client.graphics.shader.Shader;
import org.joml.Vector3f;

public record Material(Vector3f tint, float transparency, Shader shader) {
}
