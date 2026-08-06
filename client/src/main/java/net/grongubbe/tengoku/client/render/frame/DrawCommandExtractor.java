package net.grongubbe.tengoku.client.render.frame;

import net.grongubbe.tengoku.client.asset.mesh.MeshSection;
import net.grongubbe.tengoku.client.gpu.GpuResourceManager;
import net.grongubbe.tengoku.client.gpu.material.GpuMaterial;
import net.grongubbe.tengoku.client.gpu.model.GpuModel;
import net.grongubbe.tengoku.client.gpu.model.GpuModelPart;
import net.grongubbe.tengoku.client.scene.components.MeshRendererComponent;
import net.grongubbe.tengoku.client.scene.components.TransformComponent;
import org.joml.Matrix4f;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class DrawCommandExtractor {
    private final GpuResourceManager gpuResources;

    private final Matrix4f modelMatrix = new Matrix4f();

    public DrawCommandExtractor(GpuResourceManager gpuResources) {
        this.gpuResources = Objects.requireNonNull(gpuResources);
    }

    public void extract(RenderFrame frame, TransformComponent transform, MeshRendererComponent render) {
        CompletableFuture<GpuModel> future = gpuResources.get(render.model());

        if (!future.isDone()) {
            return;
        }

        GpuModel model = future.join();

        transform.matrix(modelMatrix);

        for (GpuModelPart part : model.parts()) {
            for (MeshSection section : part.mesh().subMeshes()) {
                GpuMaterial material = part.materials().get(section.materialSlot());

                frame.add(new DrawCommand(modelMatrix, part.mesh(), material, section.indexOffset(), section.indexCount()));
            }
        }
    }
}