package net.grongubbe.tengoku.client.asset.material;

import net.grongubbe.tengoku.client.asset.AssetLoader;
import net.grongubbe.tengoku.client.asset.AssetLoadingContext;
import net.grongubbe.tengoku.client.asset.serialization.material.MaterialDefinition;
import net.grongubbe.tengoku.client.asset.serialization.material.MaterialDeserializer;
import net.grongubbe.tengoku.client.asset.shader.MaterialParameterDefinition;
import net.grongubbe.tengoku.client.asset.shader.Shader;
import net.grongubbe.tengoku.client.asset.shader.ShaderKey;
import net.grongubbe.tengoku.client.asset.texture.TextureKey;
import net.grongubbe.tengoku.common.util.io.ResourceLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public final class MaterialLoader implements AssetLoader<MaterialKey, Material> {
    private final MaterialDeserializer deserializer;

    public MaterialLoader(MaterialDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    @Override
    public Material load(MaterialKey key, AssetLoadingContext context) throws IOException {
        try (InputStream stream = ResourceLoader.open(key.path())) {
            MaterialDefinition definition = deserializer.deserialize(stream);
            Shader shader = context.get(new ShaderKey(definition.shader()));

            MaterialValueBuffer values = new MaterialValueBuffer(shader.layout());

            for (MaterialParameterDefinition parameter : shader.layout().parameters()) {
                Object value = definition.parameters().getOrDefault(parameter.name(), parameter.defaultValue());

                if (value != null) {
                    values.set(parameter.name(), resolveParameterValue(parameter, value, context));
                }

                if (parameter.required() && values.get(parameter.slot()) == null) {
                    throw new IllegalStateException("Material for shader \"" + shader.path() + "\" is missing required parameter \"" + parameter.name() + "\".");
                }
            }

            for (String name : definition.parameters().keySet()) {
                if (shader.layout().parameter(name) == null) {
                    throw new IllegalStateException("Unknown material parameter \"" + name + "\".");
                }
            }

            return new Material(shader, values);
        }
    }

    private Object resolveParameterValue(MaterialParameterDefinition parameter, Object serializedValue, AssetLoadingContext context) {
        return switch (parameter.type()) {
            case FLOAT -> ((Number) serializedValue).floatValue();

            case VECTOR2 -> {
                List<?> values = (List<?>) serializedValue;

                yield new Vector2f(
                        ((Number) values.get(0)).floatValue(),
                        ((Number) values.get(1)).floatValue()
                );
            }

            case VECTOR3 -> {
                List<?> values = (List<?>) serializedValue;

                yield new Vector3f(
                        ((Number) values.get(0)).floatValue(),
                        ((Number) values.get(1)).floatValue(),
                        ((Number) values.get(2)).floatValue()
                );
            }

            case VECTOR4 -> {
                List<?> values = (List<?>) serializedValue;

                yield new Vector4f(
                        ((Number) values.get(0)).floatValue(),
                        ((Number) values.get(1)).floatValue(),
                        ((Number) values.get(2)).floatValue(),
                        ((Number) values.get(3)).floatValue()
                );
            }

            case TEXTURE -> context.get(new TextureKey(Path.of((String) serializedValue)));
        };
    }
}
