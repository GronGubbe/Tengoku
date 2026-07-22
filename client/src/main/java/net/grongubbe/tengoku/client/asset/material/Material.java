package net.grongubbe.tengoku.client.asset.material;

import net.grongubbe.tengoku.client.asset.shader.Shader;
import net.grongubbe.tengoku.client.asset.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public final class Material {
    private final Shader shader;
    private final MaterialValueBuffer values;

    public Material(Shader shader, MaterialValueBuffer values) {
        this.shader = shader;
        this.values = values;
    }

    public Shader shader() {
        return shader;
    }

    public MaterialValueBuffer values() {
        return values;
    }

    public List<Object> dependencies() {
        List<Object> dependencies = new ArrayList<>();

        dependencies.add(shader);

        for (Object value : values.values()) {
            if (value instanceof Texture texture) {
                dependencies.add(texture);
            }
        }

        return dependencies;
    }
}
