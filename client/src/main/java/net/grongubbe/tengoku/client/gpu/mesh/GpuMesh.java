package net.grongubbe.tengoku.client.gpu.mesh;

import net.grongubbe.tengoku.client.asset.mesh.MeshSection;
import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.render.RenderThread;

import java.util.List;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public final class GpuMesh implements GpuResource {
    private final int vao;
    private final int vertexBuffer;
    private final int indexBuffer;

    private final List<MeshSection> meshSections;

    public GpuMesh(int vao, int vertexBuffer, int indexBuffer, List<MeshSection> meshSections) {
        this.vao = vao;
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
        this.meshSections = List.copyOf(meshSections);
    }

    public int vao() {
        return vao;
    }

    public int vertexBuffer() {
        return vertexBuffer;
    }

    public int indexBuffer() {
        return indexBuffer;
    }

    public List<MeshSection> subMeshes() {
        return meshSections;
    }

    @Override
    public void destroy() {
        RenderThread.assertCurrent();

        glDeleteVertexArrays(vao);
        glDeleteBuffers(vertexBuffer);
        glDeleteBuffers(indexBuffer);
    }
}