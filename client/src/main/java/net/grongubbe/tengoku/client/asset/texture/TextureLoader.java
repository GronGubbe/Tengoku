package net.grongubbe.tengoku.client.asset.texture;

import net.grongubbe.tengoku.client.asset.AssetLoader;
import net.grongubbe.tengoku.client.asset.AssetLoadingContext;
import net.grongubbe.tengoku.client.asset.image.ImageData;
import net.grongubbe.tengoku.client.asset.image.ImageDecoder;
import net.grongubbe.tengoku.client.asset.serialization.AssetDeserializer;
import net.grongubbe.tengoku.client.asset.serialization.texture.TextureDefinition;
import net.grongubbe.tengoku.common.util.io.ResourceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

public final class TextureLoader implements AssetLoader<TextureKey, Texture> {
    private static final Logger LOGGER = LogManager.getLogger(TextureLoader.class);

    private final AssetDeserializer<TextureDefinition> deserializer;
    private final ImageDecoder decoder;

    public TextureLoader(AssetDeserializer<TextureDefinition> deserializer, ImageDecoder decoder) {
        this.deserializer = deserializer;
        this.decoder = decoder;
    }

    @Override
    public Texture load(TextureKey key, AssetLoadingContext context) throws IOException {
        LOGGER.info("Loading texture definition {}", key.path());

        try (InputStream stream = ResourceLoader.open(key.path())) {
            TextureDefinition definition = deserializer.deserialize(stream);

            LOGGER.info("Texture references image {}", definition.image());

            try (InputStream image = ResourceLoader.open(definition.image())) {
                ImageData data = decoder.decode(image);
                LOGGER.info("Decoded texture {}x{} {}", data.width(), data.height(), data.format());

                Texture texture = new Texture(data);

                LOGGER.info("Texture object created");

                return texture;
            }
        }
    }
}