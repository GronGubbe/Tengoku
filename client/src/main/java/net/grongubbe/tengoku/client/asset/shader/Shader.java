package net.grongubbe.tengoku.client.asset.shader;

import java.nio.file.Path;

public final class Shader {
    private final Path path;

    private final String vertexSource;
    private final String fragmentSource;

    private final ShaderLayout layout;

    public Shader(Path path, String vertexSource, String fragmentSource, ShaderLayout layout) {
        this.path = path;
        this.vertexSource = vertexSource;
        this.fragmentSource = fragmentSource;
        this.layout = layout;
    }

    public Path path() {
        return path;
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
}
