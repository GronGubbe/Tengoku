package net.grongubbe.tengoku.client.gpu;

import java.util.List;

public interface GpuUploader<A, G extends GpuResource> {
    default List<Object> dependencies(A asset) {
        return List.of();
    }

    G upload(A asset, List<GpuResource> dependencies);
}