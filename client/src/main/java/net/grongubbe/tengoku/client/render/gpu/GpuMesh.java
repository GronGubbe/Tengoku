package net.grongubbe.tengoku.client.render.gpu;

import net.grongubbe.tengoku.client.asset.assets.MeshAsset;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;

public class GpuMesh implements AutoCloseable {
    private final VertexArrayObject vao;
    private final VertexBufferObject vbo;
    private final ElementBufferObject ebo;

    private final boolean hasIndices;

    private final int vertexCount;
    private final int indexCount;

    private final int primitiveMode;

    private GpuMesh(float[] positions, int[] indices, MeshAsset.PrimitiveMode primitiveMode) {
        vao = new VertexArrayObject();
        vbo = new VertexBufferObject(positions);

        this.vertexCount = positions.length / 3;

        vao.bind();
        vbo.bind();

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0L);
        glEnableVertexAttribArray(0);

        if (indices != null) {
            hasIndices = true;

            ebo = new ElementBufferObject(indices);
            indexCount = indices.length;
        } else {
            hasIndices = false;

            ebo = null;
            indexCount = 0;
        }

        vao.unbind();
        vbo.unbind();

        this.primitiveMode = switch (primitiveMode) {
            case POINTS -> GL_POINTS;
            case LINES -> GL_LINES;
            case LINE_LOOP -> GL_LINE_LOOP;
            case LINE_STRIP -> GL_LINE_STRIP;
            case TRIANGLES -> GL_TRIANGLES;
            case TRIANGLE_STRIP -> GL_TRIANGLE_STRIP;
            case TRIANGLE_FAN -> GL_TRIANGLE_FAN;
        };
    }

    public static GpuMesh fromMeshAsset(MeshAsset meshAsset) {
        return new GpuMesh(meshAsset.getPositions(), meshAsset.getIndices(), meshAsset.getPrimitiveMode());
    }

    public void bind() {
        vao.bind();
    }

    public void unbind() {
        vao.unbind();
    }

    public boolean hasIndices() {
        return hasIndices;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getPrimitiveMode() {
        return primitiveMode;
    }

    @Override
    public void close() {
        vao.close();
        vbo.close();

        if (hasIndices) {
            ebo.close();
        }
    }
}
