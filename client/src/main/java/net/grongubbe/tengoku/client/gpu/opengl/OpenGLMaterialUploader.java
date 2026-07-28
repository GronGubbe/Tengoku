package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.asset.Asset;
import net.grongubbe.tengoku.client.asset.material.Material;
import net.grongubbe.tengoku.client.asset.shader.MaterialParameterDefinition;
import net.grongubbe.tengoku.client.asset.shader.ShaderLayout;
import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.GpuUploader;
import net.grongubbe.tengoku.client.gpu.material.GpuMaterial;
import net.grongubbe.tengoku.client.gpu.material.GpuMaterialValues;
import net.grongubbe.tengoku.client.gpu.shader.GpuShader;
import net.grongubbe.tengoku.client.render.RenderThread;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public final class OpenGLMaterialUploader implements GpuUploader<Material, GpuMaterial> {
    @Override
    public List<Asset> dependencies(Material material) {
        return material.dependencies();
    }

    @Override
    public GpuMaterial upload(Material material, Map<Asset, GpuResource> dependencies) {
        RenderThread.assertCurrent();

        GpuResource shaderResource = dependencies.get(material.shader());

        if (!(shaderResource instanceof GpuShader shader)) {
            throw new IllegalStateException("""
                    Missing GPU shader dependency.

                    Material:
                    %s

                    Shader:
                    %s
                    """.formatted(material.key().path(), material.shader().key().path())
            );
        }

        ShaderLayout layout = material.shader().layout();

        int[] uniforms = createUniformLocations(layout, shader);
        GpuMaterialValues values = createValues(material, dependencies);

        return new GpuMaterial(shader, layout, values, uniforms);
    }

    private int[] createUniformLocations(ShaderLayout layout, GpuShader shader) {
        int[] uniforms = new int[layout.size()];
        Arrays.fill(uniforms, -1);

        for (MaterialParameterDefinition parameter : layout.parameters()) {
            uniforms[parameter.slot()] = glGetUniformLocation(shader.program(), parameter.name());
        }

        return uniforms;
    }

    private GpuMaterialValues createValues(Material material, Map<Asset, GpuResource> dependencies) {
        Map<Integer, Object> values = new HashMap<>();

        for (MaterialParameterDefinition parameter : material.shader().layout().parameters()) {
            Object value = material.values().get(parameter.slot());

            if (value instanceof GpuResource) {
                throw new IllegalStateException("""
                        Material contains a GPU resource reference.
                        
                        CPU materials must not contain GPU objects.

                        Material:
                        %s

                        Parameter:
                        %s
                        """.formatted(material.key().path(), parameter.name())
                );
            }

            if (value instanceof Asset asset) {
                GpuResource gpuValue = dependencies.get(asset);

                if (gpuValue == null) {
                    throw new IllegalStateException("""
                            Missing GPU dependency.
            
                            Material:
                            %s
            
                            Parameter:
                            %s
            
                            Asset:
                            %s
                            """.formatted(material.key().path(), parameter.name(), asset.key().toString())
                    );
                }

                value = gpuValue;
            }

            values.put(parameter.slot(), value);
        }

        return new GpuMaterialValues(values);
    }
}