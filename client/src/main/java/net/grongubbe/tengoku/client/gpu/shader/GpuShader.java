package net.grongubbe.tengoku.client.gpu.shader;

import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.GpuResourceId;
import net.grongubbe.tengoku.client.render.RenderThread;

import static org.lwjgl.opengl.GL20.glDeleteProgram;

public final class GpuShader implements GpuResource {
    private final int id;

    private final int program;
    private final ShaderUniforms uniforms;

    private boolean destroyed;

    public GpuShader(int program, ShaderUniforms uniforms) {
        this.id = GpuResourceId.next();

        this.program = program;
        this.uniforms = uniforms;
    }

    public int id() {
        return id;
    }

    public int program() {
        if (destroyed) {
            throw new IllegalStateException("Shader already destroyed");
        }

        return program;
    }

    public ShaderUniforms uniforms() {
        if (destroyed) {
            throw new IllegalStateException("Shader already destroyed");
        }

        return uniforms;
    }

    @Override
    public void destroy() {
        RenderThread.assertCurrent();

        if (destroyed) {
            return;
        }

        destroyed = true;
        glDeleteProgram(program);
    }
}