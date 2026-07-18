package net.grongubbe.tengoku.client.asset.mesh;

public final class SubMesh {
    private final int indexOffset;
    private final int indexCount;
    private final int materialSlot;

    public SubMesh(int indexOffset, int indexCount, int materialSlot) {
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