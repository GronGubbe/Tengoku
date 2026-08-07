package net.grongubbe.tengoku.client.gpu.material;

import net.grongubbe.tengoku.client.asset.shader.ShaderLayout;
import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.GpuResourceId;
import net.grongubbe.tengoku.client.gpu.shader.GpuShader;

public final class GpuMaterial implements GpuResource {
    private final int id;

    private final GpuShader shader;
    private final ShaderLayout layout;
    private final GpuMaterialValues values;
    private final int[] uniformLocations;

    public GpuMaterial(GpuShader shader, ShaderLayout layout, GpuMaterialValues values, int[] uniformLocations) {
        this.id = GpuResourceId.next();

        this.shader = shader;
        this.layout = layout;
        this.values = values;
        this.uniformLocations = uniformLocations;
    }

    public int id() {
        return id;
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

    public int uniformLocation(int slot) {
        if (slot < 0 || slot >= uniformLocations.length) {
            throw new IllegalArgumentException("Invalid material uniform slot: " + slot);
        }

        return uniformLocations[slot];
    }

    @Override
    public void destroy() {
    }
}