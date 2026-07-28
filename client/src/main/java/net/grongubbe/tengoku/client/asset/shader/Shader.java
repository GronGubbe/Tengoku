package net.grongubbe.tengoku.client.asset.shader;

import net.grongubbe.tengoku.client.asset.Asset;

import java.util.Objects;

public final class Shader implements Asset {
    private final ShaderKey key;

    private final String vertexSource;
    private final String fragmentSource;

    private final ShaderLayout layout;

    public Shader(ShaderKey key, String vertexSource, String fragmentSource, ShaderLayout layout) {
        this.key = Objects.requireNonNull(key);
        this.vertexSource = Objects.requireNonNull(vertexSource);
        this.fragmentSource = Objects.requireNonNull(fragmentSource);
        this.layout = Objects.requireNonNull(layout);
    }

    @Override
    public ShaderKey key() {
        return key;
    }

    public String vertexSource() {
        return vertexSource;
    }

    public String fragmentSource() {
        return fragmentSource;
    }

    public ShaderLayout layout() {
        return layout;
    }

    @Override
    public String toString() {
        return "Shader[" + key.path() + "]";
    }
}
