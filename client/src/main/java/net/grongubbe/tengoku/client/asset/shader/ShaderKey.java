package net.grongubbe.tengoku.client.asset.shader;

import net.grongubbe.tengoku.client.asset.AssetKey;

import java.nio.file.Path;

public record ShaderKey(Path path) implements AssetKey<Shader> {
    @Override
    public Class<Shader> type() {
        return Shader.class;
    }
}
