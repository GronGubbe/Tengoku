package net.grongubbe.tengoku.client.asset.shader;

import net.grongubbe.tengoku.client.asset.texture.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public enum MaterialParameterType {
    FLOAT(Float.class),
    VECTOR2(Vector2f.class),
    VECTOR3(Vector3f.class),
    VECTOR4(Vector4f.class),
    TEXTURE(Texture.class);

    private final Class<?> valueType;

    MaterialParameterType(Class<?> valueType) {
        this.valueType = valueType;
    }

    public Class<?> valueType() {
        return valueType;
    }
}
