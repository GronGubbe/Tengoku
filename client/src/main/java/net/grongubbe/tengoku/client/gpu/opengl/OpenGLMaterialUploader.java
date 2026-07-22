package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.asset.material.Material;
import net.grongubbe.tengoku.client.asset.shader.MaterialParameterDefinition;
import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.GpuUploader;
import net.grongubbe.tengoku.client.gpu.material.GpuMaterial;
import net.grongubbe.tengoku.client.gpu.material.GpuMaterialValues;
import net.grongubbe.tengoku.client.gpu.shader.GpuShader;
import net.grongubbe.tengoku.client.render.RenderThread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public final class OpenGLMaterialUploader implements GpuUploader<Material, GpuMaterial> {

    @Override
    public List<Object> dependencies(Material material) {
        return material.dependencies();
    }

    @Override
    public GpuMaterial upload(Material material, Map<Object, GpuResource> dependencies) {
        RenderThread.assertCurrent();

        GpuShader shader = (GpuShader) dependencies.get(material.shader());

        Map<String, Integer> uniforms = createUniformLocations(material, shader);

        GpuMaterialValues values = createValues(material, dependencies);

        return new GpuMaterial(shader, material.shader().layout(), values, uniforms);
    }

    private Map<String, Integer> createUniformLocations(Material material, GpuShader shader) {
        Map<String, Integer> uniforms = new HashMap<>();

        for (MaterialParameterDefinition parameter : material.shader().layout().parameters()) {
            uniforms.put(parameter.name(), glGetUniformLocation(shader.program(), parameter.name()));
        }

        return uniforms;
    }

    private GpuMaterialValues createValues(Material material, Map<Object, GpuResource> dependencies) {
        Map<Integer, Object> values = new HashMap<>();

        for (MaterialParameterDefinition parameter : material.shader().layout().parameters()) {
            Object value = material.values().get(parameter.slot());

            if (value instanceof GpuResource) {
                throw new IllegalStateException("CPU material contains GPU resource");
            }

            GpuResource gpuValue = dependencies.get(value);

            if (gpuValue != null) {
                value = gpuValue;
            }

            values.put(parameter.slot(), value);
        }

        return new GpuMaterialValues(values);
    }
}