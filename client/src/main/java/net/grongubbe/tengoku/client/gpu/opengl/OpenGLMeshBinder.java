package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.gpu.mesh.GpuMesh;
import net.grongubbe.tengoku.client.render.RenderThread;

import static org.lwjgl.opengl.GL30.glBindVertexArray;

public final class OpenGLMeshBinder {
    public void bind(GpuMesh mesh) {
        RenderThread.assertCurrent();

        glBindVertexArray(mesh.vao());
    }

    public void unbind() {
        RenderThread.assertCurrent();

        glBindVertexArray(0);
    }
}