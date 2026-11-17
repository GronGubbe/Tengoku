package net.grongubbe.tengoku.client.render.gpu;

import static org.lwjgl.opengl.GL15C.*;

public class ElementBufferObject implements AutoCloseable {
    private final int ebo;

    public ElementBufferObject(int[] indices) {
        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW); // TODO: use try-catch here
    }

    @Override
    public void close() {
        glDeleteBuffers(ebo);
    }
}
