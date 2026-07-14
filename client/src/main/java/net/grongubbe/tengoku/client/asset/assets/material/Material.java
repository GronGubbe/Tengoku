package net.grongubbe.tengoku.client.asset.assets.material;

import net.grongubbe.tengoku.client.asset.Asset;
import net.grongubbe.tengoku.client.asset.assets.shader.Shader;

public abstract class Material implements Asset {
    protected final Shader shader;

    public Material(Shader shader) {
        this.shader = shader;
    }

    public Shader getShader() {
        return shader;
    }
}
