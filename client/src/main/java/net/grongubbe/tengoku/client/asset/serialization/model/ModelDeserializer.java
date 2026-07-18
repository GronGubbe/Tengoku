package net.grongubbe.tengoku.client.asset.serialization.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.grongubbe.tengoku.client.asset.serialization.AssetDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ModelDeserializer implements AssetDeserializer<ModelDefinition> {
    private final ObjectMapper mapper;

    public ModelDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public ModelDefinition deserialize(InputStream input) throws IOException {
        JsonNode root = mapper.readTree(input);

        JsonNode partsNode = root.get("parts");

        if (partsNode == null || !partsNode.isArray()) {
            throw new IOException("Model definition is missing required field \"parts\".");
        }

        List<ModelPartDefinition> parts = new ArrayList<>();

        for (JsonNode partNode : partsNode) {
            Path mesh = Path.of(requiredMesh(partNode));

            List<Path> materials = new ArrayList<>();

            JsonNode materialsNode = partNode.get("materials");

            if (materialsNode == null || !materialsNode.isArray()) {
                throw new IOException("Model part is missing required field \"materials\".");
            }

            for (JsonNode materialNode : materialsNode) {
                if (!materialNode.isTextual()) {
                    throw new IOException("Model material path must be a string.");
                }

                materials.add(Path.of(materialNode.asText()));
            }

            parts.add(new ModelPartDefinition(mesh, materials));
        }

        return new ModelDefinition(parts);
    }

    private String requiredMesh(JsonNode node) throws IOException {
        JsonNode value = node.get("mesh");

        if (value == null || !value.isTextual()) {
            throw new IOException("Model definition is missing required field \"mesh\".");
        }

        return value.asText();
    }
}