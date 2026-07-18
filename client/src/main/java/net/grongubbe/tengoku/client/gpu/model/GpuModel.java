package net.grongubbe.tengoku.client.gpu.model;

import net.grongubbe.tengoku.client.gpu.GpuResource;

import java.util.List;

public final class GpuModel implements GpuResource {
    private final List<GpuModelPart> parts;

    public GpuModel(List<GpuModelPart> parts) {
        this.parts = List.copyOf(parts);
    }

    public List<GpuModelPart> parts() {
        return parts;
    }
}