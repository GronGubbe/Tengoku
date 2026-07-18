package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.asset.material.Material;
import net.grongubbe.tengoku.client.asset.shader.MaterialParameterDefinition;
import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.GpuUploader;
import net.grongubbe.tengoku.client.gpu.material.GpuMaterial;
import net.grongubbe.tengoku.client.gpu.shader.GpuShader;
import net.grongubbe.tengoku.client.render.RenderThread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public final class OpenGLMaterialUploader implements GpuUploader<Material, GpuMaterial> {
    @Override
    public List<Object> dependencies(Material material) {
        return List.of(material.shader());
    }

    @Override
    public GpuMaterial upload(Material material, List<GpuResource> dependencies) {
        RenderThread.assertCurrent();

        GpuShader shader = (GpuShader) dependencies.getFirst();

        Map<String,Integer> uniforms = new HashMap<>();

        for (MaterialParameterDefinition parameter : material.shader().layout().parameters()) {
            uniforms.put(parameter.name(), glGetUniformLocation(shader.program(), parameter.name()));
        }

        return new GpuMaterial(shader, material.shader().layout(), material.values(), uniforms);
    }
}