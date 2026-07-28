package net.grongubbe.tengoku.client.gpu;

import net.grongubbe.tengoku.client.asset.Asset;

import java.util.HashMap;
import java.util.Map;

public final class GpuUploaderRegistry {
    private final Map<Class<?>, GpuUploader<?, ?>> uploaders = new HashMap<>();

    public <A extends Asset, G extends GpuResource> void register(Class<A> assetType, GpuUploader<A, G> uploader) {
        uploaders.put(assetType, uploader);
    }

    @SuppressWarnings("unchecked")
    public <A extends Asset, G extends GpuResource> GpuUploader<A, G> get(Class<A> assetType) {
        return (GpuUploader<A, G>) uploaders.get(assetType);
    }
}