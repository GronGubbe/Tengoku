package net.grongubbe.tengoku.client.asset.mesh;

import java.nio.ByteBuffer;
import java.util.Objects;

public final class VertexBuffer {
    private final ByteBuffer data;

    public VertexBuffer(ByteBuffer data) {
        this.data = Objects.requireNonNull(data, "Vertex buffer data cannot be null");

        if (!data.hasRemaining()) {
            throw new IllegalArgumentException("Vertex buffer cannot be empty");
        }
    }

    public ByteBuffer data() {
        return data;
    }

    public int byteSize() {
        return data.remaining();
    }
}