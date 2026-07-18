package net.grongubbe.tengoku.client.gpu.submesh;

public final class GpuSubMesh {
    private final int indexOffset;
    private final int indexCount;
    private final int materialSlot;

    public GpuSubMesh(int indexOffset, int indexCount, int materialSlot) {
        this.indexOffset = indexOffset;
        this.indexCount = indexCount;
        this.materialSlot = materialSlot;
    }

    public int indexOffset() {
        return indexOffset;
    }

    public int indexCount() {
        return indexCount;
    }

    public int materialSlot() {
        return materialSlot;
    }
}