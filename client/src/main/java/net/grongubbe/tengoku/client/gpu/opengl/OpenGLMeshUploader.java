package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.asset.mesh.Mesh;
import net.grongubbe.tengoku.client.asset.mesh.MeshData;
import net.grongubbe.tengoku.client.asset.mesh.VertexAttribute;
import net.grongubbe.tengoku.client.asset.mesh.VertexLayout;
import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.GpuUploader;
import net.grongubbe.tengoku.client.gpu.mesh.GpuMesh;
import net.grongubbe.tengoku.client.render.RenderThread;

import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public final class OpenGLMeshUploader implements GpuUploader<Mesh, GpuMesh> {
    @Override
    public GpuMesh upload(Mesh mesh, List<GpuResource> dependencies) {
        RenderThread.assertCurrent();

        MeshData data = mesh.data();

        int vao = glGenVertexArrays();
        int vertexBuffer = glGenBuffers();
        int indexBuffer = glGenBuffers();

        glBindVertexArray(vao);

        uploadVertexBuffer(vertexBuffer, data);
        uploadIndexBuffer(indexBuffer, data);

        configureVertexAttributes(data);

        glBindVertexArray(0);

        return new GpuMesh(vao, vertexBuffer, indexBuffer, mesh.subMeshes());
    }

    private void uploadVertexBuffer(int buffer, MeshData data) {
        glBindBuffer(GL_ARRAY_BUFFER, buffer);

        glBufferData(GL_ARRAY_BUFFER, data.vertices().data(), GL_STATIC_DRAW);
    }

    private void uploadIndexBuffer(int buffer, MeshData data) {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffer);

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data.indices().data(), GL_STATIC_DRAW);
    }

    private void configureVertexAttributes(MeshData data) {
        VertexLayout layout = data.layout();

        int offset = 0;

        for (int attributeIndex = 0; attributeIndex < layout.attributes().size(); attributeIndex++) {
            VertexAttribute attribute = layout.attributes().get(attributeIndex);

            glEnableVertexAttribArray(attributeIndex);
            glVertexAttribPointer(attributeIndex, attribute.type().componentCount(), GL_FLOAT, false, layout.stride(), offset);

            offset += attribute.type().bytes();
        }
    }
}