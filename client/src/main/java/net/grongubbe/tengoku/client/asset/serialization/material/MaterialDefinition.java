package net.grongubbe.tengoku.client.asset.serialization.material;

import java.nio.file.Path;
import java.util.Map;

public final class MaterialDefinition {
    private final Path shader;
    private final Map<String, Object> parameters;

    public MaterialDefinition(Path shader, Map<String, Object> parameters) {
        this.shader = shader;
        this.parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
    }

    public Path shader() {
        return shader;
    }

    public Map<String, Object> parameters() {
        return parameters;
    }
}