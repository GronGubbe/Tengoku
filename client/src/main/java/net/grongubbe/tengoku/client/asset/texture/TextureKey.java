package net.grongubbe.tengoku.client.asset.texture;

import net.grongubbe.tengoku.client.asset.AssetKey;

import java.nio.file.Path;

public record TextureKey(Path path) implements AssetKey<Texture> {
    @Override
    public Class<Texture> type() {
        return Texture.class;
    }
}
