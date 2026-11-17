package net.grongubbe.tengoku.client.render.gpu;

import net.grongubbe.tengoku.client.asset.assets.ShaderAsset;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20C.*;

public final class GpuShader implements AutoCloseable {
    private final int programId;

    private final Map<String, Integer> uniforms = new HashMap<>();

    private GpuShader(String vertexSrc, String fragmentSrc) {
        int vertexShader = 0;
        int fragmentShader = 0;
        int newProgram = 0;

        try {
            vertexShader = compileShader(GL_VERTEX_SHADER, vertexSrc);
            fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentSrc);

            newProgram = glCreateProgram();

            if (newProgram == 0) {
                throw new IllegalStateException("Failed to create shader program");
            }

            glAttachShader(newProgram, vertexShader);
            glAttachShader(newProgram, fragmentShader);

            glLinkProgram(newProgram);

            if (glGetProgrami(newProgram, GL_LINK_STATUS) == GL_FALSE) {
                String log = glGetProgramInfoLog(newProgram);
                throw new IllegalStateException("Shader linking failed:\n" + log);
            }

            glDetachShader(newProgram, vertexShader);
            glDetachShader(newProgram, fragmentShader);

            programId = newProgram;
            newProgram = 0;

        } finally {
            if (newProgram != 0) {
                glDeleteProgram(newProgram);
            }

            if (vertexShader != 0) {
                glDeleteShader(vertexShader);
            }

            if (fragmentShader != 0) {
                glDeleteShader(fragmentShader);
            }
        }
    }

    private static int compileShader(int type, String source) {
        int shaderId = glCreateShader(type);

        if (shaderId == 0) {
            throw new IllegalStateException("Failed to create shader");
        }

        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(shaderId);
            glDeleteShader(shaderId);

            throw new IllegalStateException("Shader compilation failed:\n" + log);
        }

        return shaderId;
    }

    public static GpuShader fromShaderAsset(ShaderAsset shaderAsset) {
        return new GpuShader(shaderAsset.getVertexSrc(), shaderAsset.getFragmentSrc());
    }

    private int getUniformLocation(String name) {
        return uniforms.computeIfAbsent(name, _ -> {
            int location = glGetUniformLocation(programId, name);

            if (location == -1) {
                throw new IllegalStateException("Couldn't find a uniform named '" + name + "' on shader: " + programId);
            }

            return location;
        });
    }

    public void setUniform(String name, Matrix4f matrix) {
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            FloatBuffer floatbuffer = memoryStack.mallocFloat(16);

            matrix.get(floatbuffer);

            glUniformMatrix4fv(getUniformLocation(name), false, floatbuffer);
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    @Override
    public void close() {
        glDeleteProgram(programId);
    }
}