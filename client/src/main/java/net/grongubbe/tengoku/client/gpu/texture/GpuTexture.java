package net.grongubbe.tengoku.client.gpu.texture;

import net.grongubbe.tengoku.client.gpu.GpuResource;

public final class GpuTexture implements GpuResource {
    private final int id;

    public GpuTexture(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}