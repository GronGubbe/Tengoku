package net.grongubbe.tengoku.client.asset.mesh;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class VertexLayout {
    private final List<VertexAttribute> attributes;
    private final int stride;

    public VertexLayout(List<VertexAttribute> attributes) {
        Objects.requireNonNull(attributes, "Vertex layout attributes cannot be null");

        if (attributes.isEmpty()) {
            throw new IllegalArgumentException("Vertex layout must contain at least one attribute");
        }

        this.attributes = List.copyOf(attributes);

        Set<String> names = new HashSet<>();

        for (VertexAttribute attribute : this.attributes) {
            Objects.requireNonNull(attribute, "Vertex layout contains a null attribute");

            if (!names.add(attribute.name())) {
                throw new IllegalArgumentException("Duplicate vertex attribute \"" + attribute.name() + "\"");
            }
        }

        this.stride = this.attributes.stream().mapToInt(attribute -> attribute.type().bytes()).sum();
    }

    public List<VertexAttribute> attributes() {
        return attributes;
    }

    public int stride() {
        return stride;
    }
}