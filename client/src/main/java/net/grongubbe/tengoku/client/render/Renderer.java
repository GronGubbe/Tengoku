package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.gpu.material.GpuMaterial;
import net.grongubbe.tengoku.client.gpu.mesh.GpuMesh;
import net.grongubbe.tengoku.client.gpu.submesh.GpuSubMesh;
import net.grongubbe.tengoku.client.gpu.model.GpuModel;
import net.grongubbe.tengoku.client.gpu.model.GpuModelPart;
import net.grongubbe.tengoku.client.gpu.opengl.OpenGLMaterialBinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public final class Renderer {
    private static final Logger LOGGER = LogManager.getLogger(Renderer.class);

    private final OpenGLMaterialBinder materialBinder;

    public Renderer() {
        this.materialBinder = new OpenGLMaterialBinder();
    }

    public void render(GpuModel model) {
        RenderThread.assertCurrent();

        LOGGER.trace("Rendering");

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        for (GpuModelPart part : model.parts()) {
            renderPart(part);
        }

        glBindVertexArray(0);
    }

    private void renderPart(GpuModelPart part) {
        GpuMesh mesh = part.mesh();

        glBindVertexArray(mesh.vao());

        for (GpuSubMesh subMesh : mesh.subMeshes()) {
            LOGGER.debug("Drawing submesh offset={} count={}", subMesh.indexOffset(), subMesh.indexCount());

            GpuMaterial material = part.materials().get(subMesh.materialSlot());

            materialBinder.bind(material);

            glDrawElements(GL_TRIANGLES, subMesh.indexCount(), GL_UNSIGNED_INT, (long) subMesh.indexOffset() * Integer.BYTES);
        }
    }
}