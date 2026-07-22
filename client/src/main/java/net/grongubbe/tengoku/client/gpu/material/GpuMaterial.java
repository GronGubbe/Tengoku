package net.grongubbe.tengoku.client.gpu.material;

import net.grongubbe.tengoku.client.asset.shader.ShaderLayout;
import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.shader.GpuShader;

import java.util.Map;

public final class GpuMaterial implements GpuResource {
    private final GpuShader shader;
    private final ShaderLayout layout;
    private final GpuMaterialValues values;
    private final Map<String, Integer> uniformLocations;

    public GpuMaterial(GpuShader shader, ShaderLayout layout, GpuMaterialValues values, Map<String, Integer> uniformLocations) {
        this.shader = shader;
        this.layout = layout;
        this.values = values;
        this.uniformLocations = Map.copyOf(uniformLocations);
    }

    public GpuShader shader() {
        return shader;
    }

    public ShaderLayout layout() {
        return layout;
    }

    public GpuMaterialValues values() {
        return values;
    }

    public int uniformLocation(String name) {
        return uniformLocations.getOrDefault(name, -1);
    }

    @Override
    public void destroy() {
    }
}