package net.grongubbe.tengoku.client.graphics.material.materials;

import net.grongubbe.tengoku.client.graphics.material.Builder;
import net.grongubbe.tengoku.client.graphics.material.MaterialCache;
import net.grongubbe.tengoku.client.graphics.shader.Shader;
import org.joml.Vector3f;

public final class UnlitColorBuilder extends Builder<UnlitColorMaterial> {
    private Vector3f tint;
    private float opacity = 1f;
    private Shader shader;

    public UnlitColorBuilder tint(Vector3f tint) {
        this.tint = tint;
        return this;
    }

    public UnlitColorBuilder opacity(float opacity) {
        this.opacity = opacity;
        return this;
    }

    public UnlitColorBuilder shader(Shader shader) {
        this.shader = shader;
        return this;
    }

    @Override
    protected void validate() {
        if (tint == null) {
            throw new IllegalStateException("tint is required");
        }

        if (shader == null) {
            throw new IllegalStateException("shader is required");
        }
    }

    @Override
    public UnlitColorMaterial build() {
        validate();

        return (UnlitColorMaterial) MaterialCache.get(
                hash(),
                () -> new UnlitColorMaterial(tint, opacity, shader)
        );
    }

    @Override
    protected long hash() {
        int h = 17;

        h = 31 * h + Float.hashCode(tint.x);
        h = 31 * h + Float.hashCode(tint.y);
        h = 31 * h + Float.hashCode(tint.z);
        h = 31 * h + Float.hashCode(opacity);

        h = 31 * h + shader.hashCode();

        return h;
    }
}