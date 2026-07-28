package net.grongubbe.tengoku.client.asset.material;

import net.grongubbe.tengoku.client.asset.AssetFormatting;
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
import java.util.stream.Collectors;

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
                    try {
                        values.set(parameter.name(), resolveParameterValue(parameter, value, context));
                    } catch (RuntimeException e) {
                        throw new IllegalStateException("""
                                Failed to resolve material parameter.
                        
                                Material:
                                %s
                        
                                Shader:
                                %s
                        
                                Parameter:
                                %s
                        
                                Expected type:
                                %s
                        
                                Value:
                                %s
                                
                                Reason:
                                %s
                                """.formatted(key.path(), shader.key().path(), parameter.name(), parameter.type(), AssetFormatting.formatValue(value), e.getMessage()), e
                        );
                    }
                }
            }

            for (String name : definition.parameters().keySet()) {
                if (shader.layout().parameter(name) == null) {
                    String available = shader.layout().parameters().stream().map(MaterialParameterDefinition::name).sorted().collect(Collectors.joining("\n"));

                    throw new IllegalStateException("""
                            Invalid material.
                            
                            Material:
                            %s
                            
                            Shader:
                            %s
                            
                            Unknown parameter:
                            %s
                            
                            Available parameters:
                            %s
                            """.formatted(key.path(), shader.key().path(), name, available)
                    );
                }
            }

            return new Material(key, shader, values);
        }
    }

    private Object resolveParameterValue(MaterialParameterDefinition parameter, Object serializedValue, AssetLoadingContext context) {
        return switch (parameter.type()) {
            case FLOAT -> ((Number) serializedValue).floatValue();

            case VECTOR2 -> {
                List<?> values = requireList(serializedValue, parameter);

                if (values.size() != 2) {
                    throw new IllegalArgumentException("Expected 2 values for VECTOR2, got " + values.size());
                }

                yield new Vector2f(
                        requireFloat(values.get(0), parameter),
                        requireFloat(values.get(1), parameter)
                );
            }

            case VECTOR3 -> {
                List<?> values = requireList(serializedValue, parameter);

                if (values.size() != 3) {
                    throw new IllegalArgumentException("Expected 3 values for VECTOR3, got " + values.size());
                }

                yield new Vector3f(
                        requireFloat(values.get(0), parameter),
                        requireFloat(values.get(1), parameter),
                        requireFloat(values.get(2), parameter)
                );
            }

            case VECTOR4 -> {
                List<?> values = requireList(serializedValue, parameter);

                if (values.size() != 4) {
                    throw new IllegalArgumentException("Expected 4 values for VECTOR4, got " + values.size());
                }

                yield new Vector4f(
                        requireFloat(values.get(0), parameter),
                        requireFloat(values.get(1), parameter),
                        requireFloat(values.get(2), parameter),
                        requireFloat(values.get(3), parameter)
                );
            }

            case TEXTURE -> context.get(new TextureKey(Path.of(requireString(serializedValue, parameter))));
        };
    }

    private List<?> requireList(Object value, MaterialParameterDefinition parameter) {
        if (!(value instanceof List<?> list)) {
            throw new IllegalArgumentException("Expected list value for " + parameter.type() + ", got " + typeName(value));
        }

        return list;
    }

    private String requireString(Object value, MaterialParameterDefinition parameter) {
        if (!(value instanceof String string)) {
            throw new IllegalArgumentException("Expected string value for " + parameter.type() + ", got " + typeName(value));
        }

        return string;
    }

    private float requireFloat(Object value, MaterialParameterDefinition parameter) {
        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException("Expected float value for " + parameter.type() + ", got " + typeName(value));
        }

        return number.floatValue();
    }

    private String typeName(Object value) {
        return value == null ? "null" : value.getClass().getSimpleName();
    }
}
