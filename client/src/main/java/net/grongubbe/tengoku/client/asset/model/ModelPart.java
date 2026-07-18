package net.grongubbe.tengoku.client.asset.model;

import net.grongubbe.tengoku.client.asset.material.Material;
import net.grongubbe.tengoku.client.asset.mesh.Mesh;

import java.util.List;

public final class ModelPart {
    private final Mesh mesh;
    private final List<Material> materials;

    public ModelPart(Mesh mesh, List<Material> materials) {
        this.mesh = mesh;
        this.materials = List.copyOf(materials);
    }

    public Mesh mesh() {
        return mesh;
    }

    public List<Material> materials() {
        return materials;
    }
}