package net.grongubbe.tengoku.client.asset.mesh;

public enum VertexAttributeType {
    FLOAT(1),
    VECTOR2(2),
    VECTOR3(3),
    VECTOR4(4);

    private final int componentCount;
    private final int bytes;

    VertexAttributeType(int componentCount) {
        this.componentCount = componentCount;
        this.bytes = componentCount * Float.BYTES;
    }

    public int componentCount() {
        return componentCount;
    }

    public int bytes() {
        return bytes;
    }
}