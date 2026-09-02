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

        GpuShader shader = requireShader(material, dependencies);

        ShaderLayout layout = material.shader().layout();

        int[] uniformLocations = createUniformLocations(layout, shader);
        GpuMaterialValues values = createGpuValues(material, dependencies, layout);

        return new GpuMaterial(shader, layout, values, uniformLocations);
    }

    private static GpuShader requireShader(Material material, Map<Asset, GpuResource> dependencies) {
        GpuResource shaderResource = dependencies.get(material.shader());

        if (shaderResource instanceof GpuShader shader) {
            return shader;
        }

        throw new IllegalStateException("""
                Missing GPU shader dependency.
                Material: %s
                Shader: %s
                """.formatted(material.key().path(), material.shader().key().path())
        );
    }

    private static int[] createUniformLocations(ShaderLayout layout, GpuShader shader) {
        int[] locations = new int[layout.size()];
        Arrays.fill(locations, -1);

        for (MaterialParameterDefinition parameter : layout.parameters()) {
            locations[parameter.slot()] = glGetUniformLocation(shader.program(), parameter.name());
        }

        return locations;
    }

    private static GpuMaterialValues createGpuValues(Material material, Map<Asset, GpuResource> dependencies, ShaderLayout layout) {
        Map<Integer, Object> values = new HashMap<>(layout.size());

        for (MaterialParameterDefinition parameter : layout.parameters()) {
            Object value = validateHasNoGpuRef(material, parameter);

            if (value instanceof Asset asset) {
                value = resolveDependency(material, parameter, asset, dependencies);
            }

            values.put(parameter.slot(), value);
        }

        return new GpuMaterialValues(values);
    }

    private static Object validateHasNoGpuRef(Material material, MaterialParameterDefinition parameter) {
        Object value = material.values().get(parameter.slot());

        if (value instanceof GpuResource) {
            throw new IllegalStateException("""
                    Material contains a GPU resource reference.
                    CPU materials must not contain GPU objects.
                    Material: %s
                    Parameter: %s
                    """.formatted(material.key().path(), parameter.name())
            );
        }
        return value;
    }

    private static GpuResource resolveDependency(Material material, MaterialParameterDefinition parameter, Asset asset, Map<Asset, GpuResource> dependencies) {
        GpuResource resource = dependencies.get(asset);

        if (resource != null) {
            return resource;
        }

        throw new IllegalStateException("""
                Missing GPU dependency.
                Material: %s
                Parameter: %s
                Asset: %s
                """.formatted(material.key().path(), parameter.name(), asset.key())
        );
    }
}