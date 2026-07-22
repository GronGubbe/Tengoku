package net.grongubbe.tengoku.client.gpu;

import java.util.List;
import java.util.Map;

public interface GpuUploader<A, G extends GpuResource> {
    default List<Object> dependencies(A asset) {
        return List.of();
    }

    G upload(A asset, Map<Object, GpuResource> dependencies);
}