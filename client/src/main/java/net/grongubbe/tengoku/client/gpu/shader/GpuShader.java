package net.grongubbe.tengoku.client.gpu.shader;

import net.grongubbe.tengoku.client.gpu.GpuResource;

import static org.lwjgl.opengl.GL20.glDeleteProgram;

public final class GpuShader implements GpuResource {
    private final int program;

    public GpuShader(int program) {
        this.program = program;
    }

    public int program() {
        return program;
    }

    @Override
    public void destroy() {
        glDeleteProgram(program);
    }
}