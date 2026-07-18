package net.grongubbe.tengoku.client.asset.material;

import net.grongubbe.tengoku.client.asset.AssetKey;

import java.nio.file.Path;

public record MaterialKey(Path path) implements AssetKey<Material> {
    @Override
    public Class<Material> type() {
        return Material.class;
    }
}
