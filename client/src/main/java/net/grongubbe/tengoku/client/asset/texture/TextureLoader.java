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
        LOGGER.debug("Loading texture definition {}", key.path());

        TextureDefinition definition;

        try (InputStream stream = ResourceLoader.open(key.path())) {
            definition = deserializer.deserialize(stream);
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    """
                    Failed to deserialize texture.
                    Texture: %s
                    Reason: %s
                    """.formatted(key.path(), exception.getMessage()), exception
            );
        }

        LOGGER.debug("Texture '{}' references image '{}'", key.path(), definition.image());

        try (InputStream image = ResourceLoader.open(definition.image())) {
            ImageData data;

            try {
                data = decoder.decode(image);
            } catch (IOException | RuntimeException exception) {
                throw new IllegalStateException(
                        """
                        Failed to decode texture.
                        Texture: %s
                        Image: %s
                        
                        Reason: %s
                        """.formatted(
                                key.path(),
                                definition.image(),
                                exception.getMessage()
                        ), exception
                );
            }

            LOGGER.debug("Decoded texture {}x{} {}", data.width(), data.height(), data.format());

            return new Texture(key, data);
        }
    }
}