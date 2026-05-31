package net.grongubbe.tengoku.client.graphics;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Mesh {
    private final float[] vertices;
    private final int[] indices;

    private final int vaoId;    // Vertex Array id

    private final int vboId;    // Vertex Buffer id
    private final int eboId;    // Element Buffer id

    private final Material material;

    public Mesh(float[] vertices, int[] indices, Material material) {
        this.vertices = vertices;
        this.indices = indices;
        this.material = material;

        vaoId = glGenVertexArrays();

        vboId = glGenBuffers();
        eboId = glGenBuffers();
        setUpVertexData(vaoId, vboId, eboId);
    }

    private void setUpVertexData(int vaoId, int vboId, int eboId) {
        glBindVertexArray(vaoId);

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        IntBuffer indicesBuffer = memAllocInt(indices.length).put(indices).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        memFree(indicesBuffer);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public int getVao() {
        return vaoId;
    }

    public int getIndicesCount() {
        return indices.length;
    }

    public Material getMaterial() {
        return material;
    }

    public void delete() {
        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(eboId);
        glDeleteBuffers(vboId);
    }
}
