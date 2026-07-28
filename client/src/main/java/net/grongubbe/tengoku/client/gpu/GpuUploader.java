package net.grongubbe.tengoku.client.gpu;

import net.grongubbe.tengoku.client.asset.Asset;

import java.util.List;
import java.util.Map;

public interface GpuUploader<A extends Asset, G extends GpuResource> {
    default List<Asset> dependencies(A asset) {
        return List.of();
    }

    G upload(A asset, Map<Asset, GpuResource> dependencies);
}