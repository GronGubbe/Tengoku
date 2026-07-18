package net.grongubbe.tengoku.client.asset.mesh;

import net.grongubbe.tengoku.client.asset.AssetKey;

import java.nio.file.Path;

public record MeshKey(Path path) implements AssetKey<Mesh> {
    @Override
    public Class<Mesh> type() {
        return Mesh.class;
    }
}