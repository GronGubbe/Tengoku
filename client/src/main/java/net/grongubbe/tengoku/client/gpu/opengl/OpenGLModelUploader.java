package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.asset.model.Model;
import net.grongubbe.tengoku.client.asset.model.ModelPart;
import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.GpuUploader;
import net.grongubbe.tengoku.client.gpu.material.GpuMaterial;
import net.grongubbe.tengoku.client.gpu.mesh.GpuMesh;
import net.grongubbe.tengoku.client.gpu.model.GpuModel;
import net.grongubbe.tengoku.client.gpu.model.GpuModelPart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OpenGLModelUploader implements GpuUploader<Model, GpuModel> {
    @Override
    public List<Object> dependencies(Model model) {
        List<Object> dependencies = new ArrayList<>();

        for (ModelPart part : model.parts()) {
            dependencies.add(part.mesh());
            dependencies.addAll(part.materials());
        }

        return dependencies;
    }

    @Override
    public GpuModel upload(Model model, List<GpuResource> dependencies) {
        Iterator<GpuResource> iterator = dependencies.iterator();

        List<GpuModelPart> parts = new ArrayList<>();

        for (ModelPart part : model.parts()) {
            GpuMesh gpuMesh = (GpuMesh) iterator.next();

            List<GpuMaterial> gpuMaterials = new ArrayList<>();

            for (int i = 0; i < part.materials().size(); i++) {
                gpuMaterials.add((GpuMaterial) iterator.next());
            }

            parts.add(new GpuModelPart(gpuMesh, gpuMaterials));
        }

        return new GpuModel(parts);
    }
}
