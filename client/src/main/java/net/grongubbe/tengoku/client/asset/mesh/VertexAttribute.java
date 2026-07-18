package net.grongubbe.tengoku.client.asset.mesh;

public final class VertexAttribute {
    private final String name;
    private final VertexAttributeType type;

    public VertexAttribute(String name, VertexAttributeType type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public VertexAttributeType type() {
        return type;
    }
}