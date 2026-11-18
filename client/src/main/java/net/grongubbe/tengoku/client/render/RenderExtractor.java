package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.asset.assets.MeshAsset;
import net.grongubbe.tengoku.client.asset.assets.ModelAsset;
import net.grongubbe.tengoku.client.ecs.Components.ModelComponent;
import net.grongubbe.tengoku.client.ecs.Components.TransformComponent;
import net.grongubbe.tengoku.client.ecs.Entity;
import net.grongubbe.tengoku.client.ecs.World;
import net.grongubbe.tengoku.client.render.gpu.GpuMesh;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RenderExtractor implements AutoCloseable {
    private final Map<MeshAsset, GpuMesh> gpuMeshes = new HashMap<>();

    public List<RenderInstance> extract(World world) {
        List<RenderInstance> renderInstances = new ArrayList<>();

        List<Entity> entities = world.query(TransformComponent.class, ModelComponent.class);

        for (Entity entity : entities) {
            TransformComponent transform = world.getComponent(entity, TransformComponent.class);
            ModelComponent model = world.getComponent(entity, ModelComponent.class);

            for (ModelAsset.ModelPart part : model.getModelAsset().getModelParts()) {
                GpuMesh gpuMesh = getOrCreateGpuMesh(part.mesh());
                Matrix4f modelMatrix = new Matrix4f();
                transform.matrix(modelMatrix);
                modelMatrix.mul(part.transform());

                RenderInstance renderInstance = new RenderInstance(gpuMesh, modelMatrix);
                renderInstances.add(renderInstance);
            }
        }

        return renderInstances;
    }

    private GpuMesh getOrCreateGpuMesh(MeshAsset meshAsset) {
        return gpuMeshes.computeIfAbsent(meshAsset, GpuMesh::fromMeshAsset);
    }

    @Override
    public void close() {
        for (GpuMesh gpuMesh : gpuMeshes.values()) {
            gpuMesh.close();
        }

        gpuMeshes.clear();
    }
}