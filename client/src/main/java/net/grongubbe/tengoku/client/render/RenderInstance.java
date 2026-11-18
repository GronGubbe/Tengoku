package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.render.gpu.GpuMesh;
import org.joml.Matrix4f;

public record RenderInstance(GpuMesh gpuMesh, Matrix4f modelMatrix) {
}
