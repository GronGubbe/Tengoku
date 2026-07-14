package net.grongubbe.tengoku.client.asset.assets.Utils;

import net.grongubbe.tengoku.client.asset.Asset;
import net.grongubbe.tengoku.client.asset.AssetId;
import net.grongubbe.tengoku.client.asset.AssetStore;
import net.grongubbe.tengoku.client.asset.assets.material.materials.UnlitMaterial;
import net.grongubbe.tengoku.client.asset.assets.material.materials.UnlitMaterialId;
import net.grongubbe.tengoku.client.asset.assets.shader.Shader;
import net.grongubbe.tengoku.client.asset.assets.shader.ShaderId;
import net.grongubbe.tengoku.client.asset.assets.shader.ShaderStage;
import net.grongubbe.tengoku.client.asset.assets.texture.Texture;
import net.grongubbe.tengoku.client.asset.assets.texture.TextureId;
import org.joml.Vector3f;

public final class Assets {
    private final AssetStore store;

    public Assets(AssetStore store) {
        this.store = store;
    }

    public Texture texture(String path) {
        return store.get(new TextureId(path));
    }

    public Shader shader(ShaderStage... stages) {
        return store.get(new ShaderId(stages));
    }

    UnlitMaterial unlitMaterial(Vector3f tint, float opacity, Shader shader) {
        return (UnlitMaterial) store.get(new UnlitMaterialId(tint, opacity, shader));
    }

    public <T extends Asset> T get(AssetId<T> id) {
        return store.get(id);
    }
}