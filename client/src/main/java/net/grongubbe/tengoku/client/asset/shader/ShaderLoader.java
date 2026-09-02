package net.grongubbe.tengoku.client.asset.shader;

import net.grongubbe.tengoku.client.asset.AssetLoader;
import net.grongubbe.tengoku.client.asset.AssetLoadingContext;
import net.grongubbe.tengoku.client.asset.serialization.shader.ShaderDefinition;
import net.grongubbe.tengoku.client.asset.serialization.shader.ShaderDeserializer;
import net.grongubbe.tengoku.common.util.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

public final class ShaderLoader implements AssetLoader<ShaderKey, Shader> {
    private final ShaderDeserializer deserializer;

    public ShaderLoader(ShaderDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    @Override
    public Shader load(ShaderKey key, AssetLoadingContext context) throws IOException {
        ShaderDefinition definition;

        try (InputStream stream = ResourceLoader.open(key.path())) {
            definition = deserializer.deserialize(stream);
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    """
                    Failed to deserialize shader.
                    Shader: %s
                    Reason: %s
                    """.formatted(key.path(), exception.getMessage()), exception
            );
        }

        String vertexSource = ResourceLoader.readString(definition.vertex());
        String fragmentSource = ResourceLoader.readString(definition.fragment());

        try {
            ShaderLayout layout = new ShaderLayout(definition.parameters());

            return new Shader(key, vertexSource, fragmentSource, layout);
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    """
                    Failed to create shader.
                    Shader: %s
                    Reason: %s
                    """.formatted(key.path(), exception.getMessage()), exception
            );
        }
    }
}