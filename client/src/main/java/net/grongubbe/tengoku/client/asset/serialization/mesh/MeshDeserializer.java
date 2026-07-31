package net.grongubbe.tengoku.client.asset.serialization.mesh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.grongubbe.tengoku.client.asset.serialization.AssetDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public final class MeshDeserializer implements AssetDeserializer<MeshDefinition> {
    private final ObjectMapper mapper;

    public MeshDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public MeshDefinition deserialize(InputStream input) throws IOException {
        JsonNode root = mapper.readTree(input);

        JsonNode mesh = root.get("mesh");

        if (mesh == null || !mesh.isTextual()) {
            throw new IOException("Mesh definition is missing required field \"mesh\".");
        }

        return new MeshDefinition(Path.of(mesh.asText()));
    }
}