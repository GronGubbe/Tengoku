package net.grongubbe.tengoku.client.asset.serialization.shader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.grongubbe.tengoku.client.asset.serialization.AssetDeserializer;
import net.grongubbe.tengoku.client.asset.shader.MaterialParameterDefinition;
import net.grongubbe.tengoku.client.asset.shader.MaterialParameterType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ShaderDeserializer implements AssetDeserializer<ShaderDefinition> {
    private final ObjectMapper mapper;

    public ShaderDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ShaderDefinition deserialize(InputStream input) throws IOException {
        JsonNode root = mapper.readTree(input);

        Path vertex = Path.of(requiredText(root, "vertex"));
        Path fragment = Path.of(requiredText(root, "fragment"));

        List<MaterialParameterDefinition> parameters = new ArrayList<>();

        JsonNode parameterArray = root.get("parameters");

        if (parameterArray != null) {
            for (JsonNode parameter : parameterArray) {
                parameters.add(new MaterialParameterDefinition(
                        parameter.get("slot").asInt(),
                        parameter.get("name").asText(),
                        MaterialParameterType.valueOf(parameter.get("type").asText()),
                        mapper.convertValue(parameter.get("defaultValue"), Object.class)
                ));
            }
        }

        return new ShaderDefinition(vertex, fragment, parameters);
    }

    private String requiredText(JsonNode root, String name) throws IOException {
        JsonNode node = root.get(name);

        if (node == null || !node.isTextual()) {
            throw new IOException("Shader definition is missing required field \"" + name + "\".");
        }

        return node.asText();
    }
}