package net.grongubbe.tengoku.client.asset.assets.mesh;

import net.grongubbe.tengoku.client.asset.AssetId;
import net.grongubbe.tengoku.client.asset.AssetStore;
import net.grongubbe.tengoku.client.asset.assets.material.Material;

public record MeshId(float[] vertices, int[] indices, Material material) implements AssetId<Mesh> {
    @Override
    public Mesh create(AssetStore assets) {
        return new Mesh(vertices, indices, material);
    }
}
