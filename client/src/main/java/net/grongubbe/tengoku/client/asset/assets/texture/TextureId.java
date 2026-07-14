package net.grongubbe.tengoku.client.asset.assets.texture;

import net.grongubbe.tengoku.client.asset.AssetId;
import net.grongubbe.tengoku.client.asset.AssetStore;

public record TextureId(String path) implements AssetId<Texture> {
    @Override
    public Texture create(AssetStore assets) {
        return new Texture(path);
    }
}