package net.grongubbe.tengoku.client.gpu.model;

import net.grongubbe.tengoku.client.gpu.material.GpuMaterial;
import net.grongubbe.tengoku.client.gpu.mesh.GpuMesh;

import java.util.List;

public final class GpuModelPart {
    private final GpuMesh mesh;
    private final List<GpuMaterial> materials;

    public GpuModelPart(GpuMesh mesh, List<GpuMaterial> materials) {
        this.mesh = mesh;
        this.materials = List.copyOf(materials);
    }

    public GpuMesh mesh() {
        return mesh;
    }

    public List<GpuMaterial> materials() {
        return materials;
    }
}