package net.grongubbe.tengoku.client.asset.serialization.shader;

import net.grongubbe.tengoku.client.asset.shader.MaterialParameterDefinition;

import java.nio.file.Path;
import java.util.List;

public final class ShaderDefinition {
    private final Path vertex;
    private final Path fragment;
    private final List<MaterialParameterDefinition> parameters;

    public ShaderDefinition(Path vertex, Path fragment, List<MaterialParameterDefinition> parameters) {
        this.vertex = vertex;
        this.fragment = fragment;
        this.parameters = List.copyOf(parameters);
    }

    public Path vertex() {
        return vertex;
    }

    public Path fragment() {
        return fragment;
    }

    public List<MaterialParameterDefinition> parameters() {
        return parameters;
    }
}