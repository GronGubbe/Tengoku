package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.asset.Asset;
import net.grongubbe.tengoku.client.asset.material.Material;
import net.grongubbe.tengoku.client.asset.model.Model;
import net.grongubbe.tengoku.client.asset.model.ModelPart;
import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.GpuUploader;
import net.grongubbe.tengoku.client.gpu.material.GpuMaterial;
import net.grongubbe.tengoku.client.gpu.mesh.GpuMesh;
import net.grongubbe.tengoku.client.gpu.model.GpuModel;
import net.grongubbe.tengoku.client.gpu.model.GpuModelPart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class OpenGLModelUploader implements GpuUploader<Model, GpuModel> {
    @Override
    public List<Asset> dependencies(Model model) {
        List<Asset> dependencies = new ArrayList<>();

        for (ModelPart part : model.parts()) {
            dependencies.add(part.mesh());
            dependencies.addAll(part.materials());
        }

        return dependencies;
    }

    @Override
    public GpuModel upload(Model model, Map<Asset, GpuResource> dependencies) {
        List<GpuModelPart> parts = new ArrayList<>();

        for (ModelPart part : model.parts()) {
            GpuMesh mesh = resolveMesh(model, part, dependencies);

            List<GpuMaterial> materials = new ArrayList<>();

            for (Material material : part.materials()) {
                materials.add(resolveMaterial(model, material, dependencies));
            }

            parts.add(new GpuModelPart(mesh, materials));
        }

        return new GpuModel(parts);
    }

    private GpuMesh resolveMesh(Model model, ModelPart part, Map<Asset, GpuResource> dependencies) {
        GpuResource resource = dependencies.get(part.mesh());

        if (!(resource instanceof GpuMesh mesh)) {
            throw new IllegalStateException("""
                    Missing GPU mesh dependency.

                    Model:
                    %s

                    Mesh:
                    %s
                    """.formatted(model.key().path(), part.mesh().key().path())
            );
        }

        return mesh;
    }

    private GpuMaterial resolveMaterial(Model model, Material material, Map<Asset, GpuResource> dependencies) {
        GpuResource resource = dependencies.get(material);

        if (!(resource instanceof GpuMaterial gpuMaterial)) {
            throw new IllegalStateException("""
                    Missing GPU material dependency.

                    Model:
                    %s

                    Material:
                    %s
                    """.formatted(model.key().path(), material.key().path())
            );
        }

        return gpuMaterial;
    }
}