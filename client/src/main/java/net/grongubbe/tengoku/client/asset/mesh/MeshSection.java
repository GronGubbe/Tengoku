package net.grongubbe.tengoku.client.asset.mesh;

public final class MeshSection {
    private final int indexOffset;
    private final int indexCount;
    private final int materialSlot;

    public MeshSection(int indexOffset, int indexCount, int materialSlot) {
        if (indexOffset < 0) {
            throw new IllegalArgumentException(
                    "MeshSection indexOffset must be >= 0 (was " + indexOffset + ")");
        }

        if (indexCount <= 0) {
            throw new IllegalArgumentException("MeshSection indexCount must be > 0 (was " + indexCount + ")");
        }

        if (materialSlot < 0) {
            throw new IllegalArgumentException("MeshSection materialSlot must be >= 0 (was " + materialSlot + ")");
        }

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