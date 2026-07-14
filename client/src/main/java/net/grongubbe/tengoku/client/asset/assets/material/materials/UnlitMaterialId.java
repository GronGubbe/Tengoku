package net.grongubbe.tengoku.client.asset.assets.material.materials;

import net.grongubbe.tengoku.client.asset.AssetId;
import net.grongubbe.tengoku.client.asset.AssetStore;
import net.grongubbe.tengoku.client.asset.assets.material.Material;
import net.grongubbe.tengoku.client.asset.assets.shader.Shader;
import org.joml.Vector3f;

public record UnlitMaterialId(Vector3f tint, float opacity, Shader shader) implements AssetId<Material> {
    @Override
    public Material create(AssetStore assets) {
        return new UnlitMaterial(tint, opacity, shader);
    }
}