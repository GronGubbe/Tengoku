package net.grongubbe.tengoku.client.gpu.mesh;

import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.submesh.GpuSubMesh;

import java.util.List;

public final class GpuMesh implements GpuResource {
    private final int vao;
    private final int vertexBuffer;
    private final int indexBuffer;

    private final List<GpuSubMesh> subMeshes;

    public GpuMesh(int vao, int vertexBuffer, int indexBuffer, List<GpuSubMesh> subMeshes) {
        this.vao = vao;
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
        this.subMeshes = List.copyOf(subMeshes);
    }

    public List<GpuSubMesh> subMeshes() {
        return subMeshes;
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
}