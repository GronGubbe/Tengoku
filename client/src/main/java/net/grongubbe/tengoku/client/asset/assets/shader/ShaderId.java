package net.grongubbe.tengoku.client.asset.assets.shader;

import net.grongubbe.tengoku.client.asset.AssetId;
import net.grongubbe.tengoku.client.asset.AssetStore;

public record ShaderId(ShaderStage... stages) implements AssetId<Shader> {
    @Override
    public Shader create(AssetStore assets) {
        return new Shader(stages);
    }
}