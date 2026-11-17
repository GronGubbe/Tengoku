package net.grongubbe.tengoku.client.render.gpu;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15C.*;

public class VertexBufferObject implements AutoCloseable {
    private final int vbo;

    public VertexBufferObject(float[] vertices) {
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices);
        buffer.flip();

        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
    }

    public void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void close() {
        glDeleteBuffers(vbo);
    }
}
