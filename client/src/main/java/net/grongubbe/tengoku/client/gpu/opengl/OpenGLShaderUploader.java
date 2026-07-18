package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.asset.shader.Shader;
import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.GpuUploader;
import net.grongubbe.tengoku.client.gpu.shader.GpuShader;
import net.grongubbe.tengoku.client.render.RenderThread;

import java.util.List;

import static org.lwjgl.opengl.GL20.*;

public final class OpenGLShaderUploader implements GpuUploader<Shader, GpuShader> {
    @Override
    public GpuShader upload(Shader shader, List<GpuResource> dependencies) {
        RenderThread.assertCurrent();

        int vertexShader = compileShader(GL_VERTEX_SHADER, shader.vertexSource());
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, shader.fragmentSource());

        int program = glCreateProgram();

        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);

        glLinkProgram(program);

        validateProgram(program);

        glDetachShader(program, vertexShader);
        glDetachShader(program, fragmentShader);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return new GpuShader(program);
    }

    private int compileShader(int type, String source) {
        int shader = glCreateShader(type);

        glShaderSource(shader, source);
        glCompileShader(shader);

        validateShader(shader);

        return shader;
    }

    private void validateShader(int shader) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Shader compilation failed:\n" + glGetShaderInfoLog(shader));
        }
    }

    private void validateProgram(int program) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            throw new IllegalStateException("Shader linking failed:\n" + glGetProgramInfoLog(program));
        }
    }
}