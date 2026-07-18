package net.grongubbe.tengoku.client.asset.mesh;

import java.nio.IntBuffer;

public final class IndexBuffer {
    private final IntBuffer data;

    public IndexBuffer(IntBuffer data) {
        this.data = data;
    }

    public IntBuffer data() {
        return data;
    }
}