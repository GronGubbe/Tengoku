package net.grongubbe.tengoku.client.asset.material;

import net.grongubbe.tengoku.client.asset.Asset;
import net.grongubbe.tengoku.client.asset.shader.Shader;
import net.grongubbe.tengoku.client.asset.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public final class Material implements Asset {
    private final MaterialKey key;

    private final Shader shader;
    private final MaterialValueBuffer values;

    public Material(MaterialKey key, Shader shader, MaterialValueBuffer values) {
        if (shader == null) {
            throw new IllegalArgumentException("Material shader cannot be null");
        }

        if (values == null) {
            throw new IllegalArgumentException("Material values cannot be null");
        }

        if (shader.layout() != values.layout()) {
            throw new IllegalArgumentException("Material values were created for a different shader layout");
        }

        this.key = key;
        this.shader = shader;
        this.values = values;
    }

    @Override
    public MaterialKey key() {
        return key;
    }

    public Shader shader() {
        return shader;
    }

    public MaterialValueBuffer values() {
        return values;
    }

    public List<Asset> dependencies() {
        List<Asset> dependencies = new ArrayList<>();

        dependencies.add(shader);

        for (Object value : values.values()) {
            if (value instanceof Texture texture) {
                dependencies.add(texture);
            }
        }

        return dependencies;
    }

    @Override
    public String toString() {
        return "Material[" + key.path() + "]";
    }
}
