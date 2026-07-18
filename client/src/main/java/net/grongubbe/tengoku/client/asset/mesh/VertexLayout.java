package net.grongubbe.tengoku.client.asset.mesh;

import java.util.List;

public final class VertexLayout {
    private final List<VertexAttribute> attributes;
    private final int stride;

    public VertexLayout(List<VertexAttribute> attributes) {
        this.attributes = List.copyOf(attributes);

        this.stride = attributes.stream().mapToInt(attribute -> attribute.type().bytes()).sum();
    }

    public List<VertexAttribute> attributes() {
        return attributes;
    }

    public int stride() {
        return stride;
    }
}