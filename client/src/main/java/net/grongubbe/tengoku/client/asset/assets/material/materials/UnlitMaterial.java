package net.grongubbe.tengoku.client.asset.assets.material.materials;

import net.grongubbe.tengoku.client.asset.assets.material.Material;
import net.grongubbe.tengoku.client.asset.assets.shader.Shader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

public final class UnlitMaterial extends Material {
    private static final Logger LOGGER = LogManager.getLogger(UnlitMaterial.class);
    private final Vector3f tint;
    private final float opacity;

    UnlitMaterial(Vector3f tint, float opacity, Shader shader) {
        super(shader);
        this.opacity = opacity;
        this.tint = tint;
        LOGGER.info("Created UnlitMaterial [tint=({},{},{}), opacity={}]", tint.x, tint.y, tint.z, opacity);
    }

    public Vector3f getTint() {
        return tint;
    }

    public float getOpacity() {
        return opacity;
    }
}
