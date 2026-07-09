package net.grongubbe.tengoku.client.graphics.material.materials;

import net.grongubbe.tengoku.client.graphics.material.Material;
import net.grongubbe.tengoku.client.graphics.material.MaterialType;
import net.grongubbe.tengoku.client.graphics.shader.Shader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

public final class UnlitColorMaterial extends Material {
    private static final Logger LOGGER = LogManager.getLogger(UnlitColorMaterial.class);
    private final Vector3f tint;
    private final float opacity;

    UnlitColorMaterial(Vector3f tint, float opacity, Shader shader) {
        super(shader);
        this.opacity = opacity;
        this.tint = tint;
        LOGGER.info("Created UnlitColorMaterial [tint=({},{},{}), opacity={}]", tint.x, tint.y, tint.z, opacity);
    }

    public Vector3f getTint() {
        return tint;
    }

    public float getOpacity() {
        return opacity;
    }

    @Override
    public MaterialType type() {
        return MaterialType.UNLITCOLOR;
    }
}
