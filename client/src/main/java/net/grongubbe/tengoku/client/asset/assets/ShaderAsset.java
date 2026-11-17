package net.grongubbe.tengoku.client.asset.assets;

import net.grongubbe.tengoku.client.asset.IAsset;

import java.util.Objects;

public class ShaderAsset implements IAsset {
    private final String name;

    private final String vertexSrc;
    private final String fragmentSrc;

    public ShaderAsset(String name, String vertexSrc, String fragmentSrc) {
        this.name = name;
        this.vertexSrc = Objects.requireNonNull(vertexSrc, "vertexSrc");
        this.fragmentSrc = Objects.requireNonNull(fragmentSrc, "fragmentSrc");
    }

    public String getName() {
        return name;
    }

    public String getVertexSrc() {
        return vertexSrc;
    }

    public String getFragmentSrc() {
        return fragmentSrc;
    }
}
