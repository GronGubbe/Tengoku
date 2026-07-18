package net.grongubbe.tengoku.client.asset.mesh;

import java.nio.ByteBuffer;

public final class VertexBuffer {
    private final ByteBuffer data;

    public VertexBuffer(ByteBuffer data) {
        this.data = data;
    }

    public ByteBuffer data() {
        return data;
    }
}