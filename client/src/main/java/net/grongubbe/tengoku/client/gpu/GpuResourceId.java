package net.grongubbe.tengoku.client.gpu;

import java.util.concurrent.atomic.AtomicInteger;

public final class GpuResourceId {
    private static final AtomicInteger NEXT_ID = new AtomicInteger();

    private GpuResourceId() {
    }

    public static int next() {
        return NEXT_ID.getAndIncrement();
    }
}