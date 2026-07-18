package net.grongubbe.tengoku.client.asset.material;

import net.grongubbe.tengoku.client.asset.shader.Shader;

public final class Material {
    private final Shader shader;
    private final MaterialValueBuffer values;

    public Material(Shader shader, MaterialValueBuffer values) {
        this.shader = shader;
        this.values = values;
    }

    public Shader shader() {
        return shader;
    }

    public MaterialValueBuffer values() {
        return values;
    }
}
