package net.grongubbe.tengoku.client.graphics.shader.shaders;

import net.grongubbe.tengoku.client.graphics.material.Material;
import net.grongubbe.tengoku.client.graphics.material.MaterialType;
import net.grongubbe.tengoku.client.graphics.material.materials.UnlitColorMaterial;
import net.grongubbe.tengoku.client.graphics.shader.Shader;
import net.grongubbe.tengoku.client.graphics.shader.ShaderFactory;
import net.grongubbe.tengoku.client.graphics.shader.ShaderStage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL20.*;

@ShaderFactory(UnlitShaderDescriptor.class)
public class UnlitShader extends Shader {
    private static final Logger LOGGER = LogManager.getLogger(UnlitShader.class);

    private int uTintLocation = -1, uOpacityLocation = -1;

    UnlitShader(UnlitShaderDescriptor ignoredDescription) {
        super(
                new ShaderStage(GL_VERTEX_SHADER, "shaders/unlit.vert"),
                new ShaderStage(GL_FRAGMENT_SHADER, "shaders/unlit.frag")
        );

        bind(MaterialType.UNLITCOLOR, m -> {
            UnlitColorMaterial material = (UnlitColorMaterial) m;

            setTintUniform(material.getTint());
            setOpacityUniform(material.getOpacity());
        });

        loadUniformLocations();
    }

    @Override
    public void setUniforms(Material material) {
        Handler handler = handlers[material.type().ordinal()];

        if (handler == null) {
            throw new IllegalArgumentException("Material not supported: " + material.getClass().getSimpleName());
        }

        handler.apply(material);
    }

    private void setTintUniform(Vector3f tint) {
        if (uTintLocation == -1) {
            LOGGER.warn("Tried to set tint uniform while tint uniform location was not loaded");
        } else {
            setVec3(uTintLocation, tint);
        }
    }

    private void setOpacityUniform(float opacity) {
        if (uOpacityLocation == -1) {
            LOGGER.warn("Tried to set opacity uniform while opacity uniform location was not loaded");
        } else {
            setFloat(uOpacityLocation, opacity);
        }
    }

    @Override
    public void loadUniformLocations() {
        uTintLocation = glGetUniformLocation(id, "uTint");
        uOpacityLocation = glGetUniformLocation(id, "uOpacity");
        LOGGER.info("Loaded uniform locations [tint={}, opacity={}]", uTintLocation, uOpacityLocation);
    }
}
