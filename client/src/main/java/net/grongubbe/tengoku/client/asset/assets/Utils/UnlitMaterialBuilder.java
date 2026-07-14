package net.grongubbe.tengoku.client.asset.assets.Utils;

import net.grongubbe.tengoku.client.asset.Builder;
import net.grongubbe.tengoku.client.asset.assets.material.materials.UnlitMaterial;
import net.grongubbe.tengoku.client.asset.assets.shader.Shader;
import org.joml.Vector3f;

public class UnlitMaterialBuilder extends Builder<UnlitMaterial> {
    private Vector3f tint = new Vector3f(1);
    private float opacity = 1;
    private Shader shader;

    public UnlitMaterialBuilder tint(Vector3f tint) {
        this.tint = tint;
        return this;
    }

    public UnlitMaterialBuilder opacity(float opacity) {
        this.opacity = opacity;
        return this;
    }

    public UnlitMaterialBuilder shader(Shader shader) {
        this.shader = shader;
        return this;
    }

    @Override
    public UnlitMaterial build(Assets assets) {
        return assets.unlitMaterial(tint, opacity, shader);
    }

    @Override
    protected void validate() {
        if (shader == null) {
            throw new IllegalStateException("shader is required");
        }
    }
}