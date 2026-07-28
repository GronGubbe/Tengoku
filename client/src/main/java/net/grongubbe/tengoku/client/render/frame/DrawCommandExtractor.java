package net.grongubbe.tengoku.client.render.frame;

import net.grongubbe.tengoku.client.asset.mesh.MeshSection;
import net.grongubbe.tengoku.client.gpu.material.GpuMaterial;
import net.grongubbe.tengoku.client.gpu.model.GpuModel;
import net.grongubbe.tengoku.client.gpu.model.GpuModelPart;
import net.grongubbe.tengoku.client.scene.Transform;
import org.joml.Matrix4f;

public final class DrawCommandExtractor {
    public static void extract(RenderFrame frame, GpuModel model, Transform transform) {
        Matrix4f modelMatrix = transform.matrix(new Matrix4f());

        for (GpuModelPart part : model.parts()) {
            for (MeshSection section : part.mesh().subMeshes()) {
                GpuMaterial material = part.materials().get(section.materialSlot());

                frame.add(new DrawCommand(modelMatrix, part.mesh(), material, section.indexOffset(), section.indexCount()));
            }
        }
    }
}