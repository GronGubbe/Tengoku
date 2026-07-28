package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.asset.Asset;
import net.grongubbe.tengoku.client.asset.mesh.Mesh;
import net.grongubbe.tengoku.client.asset.mesh.MeshData;
import net.grongubbe.tengoku.client.asset.mesh.VertexAttribute;
import net.grongubbe.tengoku.client.asset.mesh.VertexLayout;
import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.GpuUploader;
import net.grongubbe.tengoku.client.gpu.mesh.GpuMesh;
import net.grongubbe.tengoku.client.render.RenderThread;

import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public final class OpenGLMeshUploader implements GpuUploader<Mesh, GpuMesh> {
    @Override
    public GpuMesh upload(Mesh mesh, Map<Asset, GpuResource> dependencies) {
        RenderThread.assertCurrent();

        MeshData data = mesh.data();

        int vao = glGenVertexArrays();
        int vertexBuffer = glGenBuffers();
        int indexBuffer = glGenBuffers();

        if (vao == 0) {
            throw new IllegalStateException("""
                    Failed to create OpenGL vertex array.

                    Mesh:
                    %s
                    """.formatted(mesh.key().path())
            );
        }

        if (vertexBuffer == 0) {
            glDeleteVertexArrays(vao);

            throw new IllegalStateException("""
                    Failed to create OpenGL vertex buffer.

                    Mesh:
                    %s
                    """.formatted(mesh.key().path())
            );
        }

        if (indexBuffer == 0) {
            glDeleteBuffers(vertexBuffer);
            glDeleteVertexArrays(vao);

            throw new IllegalStateException("""
                    Failed to create OpenGL index buffer.

                    Mesh:
                    %s
                    """.formatted(mesh.key().path())
            );
        }

        try {
            glBindVertexArray(vao);

            uploadVertexBuffer(vertexBuffer, data);
            uploadIndexBuffer(indexBuffer, data);

            configureVertexAttributes(data);

            validateUpload(mesh);

            glBindVertexArray(0);

            return new GpuMesh(vao, vertexBuffer, indexBuffer, mesh.subMeshes());
        } catch (RuntimeException e) {
            glBindVertexArray(0);

            glDeleteBuffers(indexBuffer);
            glDeleteBuffers(vertexBuffer);
            glDeleteVertexArrays(vao);

            throw e;
        }
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

    private void validateUpload(Mesh mesh) {
        int error = glGetError();

        if (error != GL_NO_ERROR) {
            throw new IllegalStateException("""
                    OpenGL mesh upload failed.

                    Mesh:
                    %s

                    OpenGL error:
                    %s
                    """.formatted(mesh.key().path(), OpenGLUtils.errorName(error))
            );
        }
    }
}