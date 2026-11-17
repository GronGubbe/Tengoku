package net.grongubbe.tengoku.client.render.gpu;

import static org.lwjgl.opengl.GL30C.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

public class VertexArrayObject implements AutoCloseable {
    private final int vao;

    public VertexArrayObject() {
        vao = glGenVertexArrays();
    }

    public void bind() {
        glBindVertexArray(vao);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    @Override
    public void close() {
        glDeleteVertexArrays(vao);
    }
}
