package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.asset.Asset;
import net.grongubbe.tengoku.client.asset.shader.MaterialParameterDefinition;
import net.grongubbe.tengoku.client.asset.shader.Shader;
import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.GpuUploader;
import net.grongubbe.tengoku.client.gpu.shader.GpuShader;
import net.grongubbe.tengoku.client.render.RenderThread;

import java.util.Map;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL20.*;

public final class OpenGLShaderUploader implements GpuUploader<Shader, GpuShader> {
    @Override
    public GpuShader upload(Shader shader, Map<Asset, GpuResource> dependencies) {
        RenderThread.assertCurrent();

        int vertexShader = compileShader(GL_VERTEX_SHADER, shader.vertexSource(), shader.key().path().toString());
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, shader.fragmentSource(), shader.key().path().toString());

        int program = glCreateProgram();

        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);

        glLinkProgram(program);

        try {
            validateProgram(program, shader);
            validateUniforms(shader, program);
        } catch (RuntimeException e) {
            glDeleteProgram(program);
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            throw e;
        }

        glDetachShader(program, vertexShader);
        glDetachShader(program, fragmentShader);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return new GpuShader(program, new OpenGLShaderUniforms(program));
    }

    private int compileShader(int type, String source, String asset) {
        int shader = glCreateShader(type);

        glShaderSource(shader, source);
        glCompileShader(shader);

        validateShader(shader, type, asset);

        return shader;
    }

    private void validateShader(int shader, int type, String asset) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            String stage = switch (type) {
                case GL_VERTEX_SHADER -> "Vertex";
                case GL_FRAGMENT_SHADER -> "Fragment";
                default -> "Unknown";
            };

            throw new IllegalStateException("""
                    Shader compilation failed.
                    Shader: %s
                    Stage: %s
                    Compiler log: %s
                    """.formatted(asset, stage, glGetShaderInfoLog(shader).trim())
            );
        }
    }

    private void validateProgram(int program, Shader shader) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            throw new IllegalStateException("""
                    Shader linking failed.
                    Shader: %s
                    Linker log: %s
                    """.formatted(shader.key().path(), glGetProgramInfoLog(program).trim())
            );
        }
    }

    private void validateUniforms(Shader shader, int program) {
        for (MaterialParameterDefinition parameter : shader.layout().parameters()) {
            int location = glGetUniformLocation(program, parameter.name());

            if (location != -1) {
                continue;
            }

            String expected = shader.layout().parameters().stream()
                    .map(MaterialParameterDefinition::name)
                    .sorted()
                    .map(name -> "        " + name)
                    .collect(Collectors.joining("\n"));

            throw new IllegalStateException("""
                    Shader validation failed.
                    Shader: %s
                    Missing uniform: %s
                    Expected type: %s
                    Expected uniforms:
                    %s
                    """.formatted(shader.key().path(), parameter.name(), parameter.type(), expected)
            );
        }
    }
}