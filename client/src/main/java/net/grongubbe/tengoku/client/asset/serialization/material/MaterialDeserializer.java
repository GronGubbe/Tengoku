package net.grongubbe.tengoku.client.asset.serialization.material;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.grongubbe.tengoku.client.asset.serialization.AssetDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class MaterialDeserializer implements AssetDeserializer<MaterialDefinition> {
    private final ObjectMapper mapper;

    public MaterialDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public MaterialDefinition deserialize(InputStream input) throws IOException {
        JsonNode root = mapper.readTree(input);

        JsonNode shader = root.get("shader");

        if (shader == null || !shader.isTextual()) {
            throw new IOException("Material definition is missing required field \"shader\".");
        }

        Map<String, Object> parameters = new HashMap<>();

        JsonNode parameterNode = root.get("parameters");

        if (parameterNode != null) {
            for (Map.Entry<String, JsonNode> entry : parameterNode.properties()) {
                parameters.put(entry.getKey(), mapper.convertValue(entry.getValue(), Object.class));
            }
        }

        return new MaterialDefinition(Path.of(shader.asText()), parameters);
    }
}