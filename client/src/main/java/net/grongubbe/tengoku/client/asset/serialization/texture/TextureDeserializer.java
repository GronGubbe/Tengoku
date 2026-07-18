package net.grongubbe.tengoku.client.asset.serialization.texture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.grongubbe.tengoku.client.asset.serialization.AssetDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public final class TextureDeserializer implements AssetDeserializer<TextureDefinition> {
    private final ObjectMapper mapper;

    public TextureDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public TextureDefinition deserialize(InputStream input) throws IOException {
        JsonNode root = mapper.readTree(input);

        JsonNode image = root.get("image");

        if (image == null || !image.isTextual()) {
            throw new IOException("Texture definition is missing required field \"image\".");
        }

        return new TextureDefinition(Path.of(image.asText()));
    }
}