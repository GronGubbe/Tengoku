package net.grongubbe.tengoku.client.asset.mesh;

import java.util.Objects;

public final class VertexAttribute {
    private final String name;
    private final VertexAttributeType type;

    public VertexAttribute(String name, VertexAttributeType type) {
        this.name = Objects.requireNonNull(name, "Vertex attribute name cannot be null");
        this.type = Objects.requireNonNull(type, "Vertex attribute type cannot be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Vertex attribute name cannot be blank");
        }
    }

    public String name() {
        return name;
    }

    public VertexAttributeType type() {
        return type;
    }
}