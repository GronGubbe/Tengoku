package net.grongubbe.tengoku.client.asset.mesh;

import java.nio.IntBuffer;
import java.util.Objects;

public final class IndexBuffer {
    private final IntBuffer data;

    public IndexBuffer(IntBuffer data) {
        this.data = Objects.requireNonNull(data, "Index buffer data cannot be null");

        if (!data.hasRemaining()) {
            throw new IllegalArgumentException("Index buffer cannot be empty");
        }
    }

    public IntBuffer data() {
        return data;
    }

    public int count() {
        return data.remaining();
    }
}