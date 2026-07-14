package net.grongubbe.tengoku.client.asset.assets.shader;

import net.grongubbe.tengoku.common.util.io.ResourceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_CONTROL_SHADER;
import static org.lwjgl.opengl.GL40.GL_TESS_EVALUATION_SHADER;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;

public final class ShaderStage {
    private static final Logger LOGGER = LogManager.getLogger(ShaderStage.class);
    private final int id;
    private final String typeName;
    private final String path;

    public ShaderStage(int type, String path) {
        this.typeName = shaderTypeName(type);
        this.path = path;

        LOGGER.info("Loading shader stage [path={}, type={}]", path, typeName);
        String source = loadShader(path);

        this.id = glCreateShader(type);

        if (id == 0) {
            throw new IllegalStateException("Failed to create shader: " + path + ", " + typeName);
        }

        glShaderSource(id, source);
        glCompileShader(id);

        if (glGetShaderi(id, GL_COMPILE_STATUS) == 0) {
            String log = glGetShaderInfoLog(id);
            delete();
            String error = """
            Failed to compile shader:
              File: %s
              Type: %s
            
            %s
            """.formatted(path, typeName, log);
            throw new IllegalStateException(error);
        }
        
        LOGGER.info("Shader stage compiled successfully [path={}, type={}]", path, typeName);
    }

    private static String shaderTypeName(int type) {
        return switch (type) {
            case GL_VERTEX_SHADER                  -> "Vertex Shader";
            case GL_TESS_CONTROL_SHADER            -> "Tessellation Control Shader";
            case GL_TESS_EVALUATION_SHADER         -> "Tessellation Evaluation Shader";
            case GL_GEOMETRY_SHADER                -> "Geometry Shader";
            case GL_FRAGMENT_SHADER                -> "Fragment Shader";
            case GL_COMPUTE_SHADER                 -> "Compute Shader";
            default -> "Unknown Shader Type (0x" + Integer.toHexString(type) + ")";
        };
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getTypeName() {
        return typeName;
    }

    private String loadShader(String path) {
        try {
            return ResourceLoader.readString(path);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load shader: " + path, e);
        }
    }

    public void delete() {
        glDeleteShader(id);
    }
}