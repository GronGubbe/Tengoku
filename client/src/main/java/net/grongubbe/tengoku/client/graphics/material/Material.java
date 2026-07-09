package net.grongubbe.tengoku.client.graphics.material;

import net.grongubbe.tengoku.client.graphics.shader.Shader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Material {
    private static final Logger LOGGER = LogManager.getLogger(Material.class);
    protected final Shader shader;

    public Material(Shader shader) {
        this.shader = shader;
        LOGGER.info("Created Material [type={}]", type());
    }

    public Shader getShader() {
        return shader;
    }

    public abstract MaterialType type();
}
