package net.grongubbe.tengoku.client.gpu;

import java.util.HashMap;
import java.util.Map;

public final class GpuUploaderRegistry {
    private final Map<Class<?>, GpuUploader<?, ?>> uploaders = new HashMap<>();

    public <A, G extends GpuResource> void register(Class<A> assetType, GpuUploader<A, G> uploader) {
        uploaders.put(assetType, uploader);
    }

    @SuppressWarnings("unchecked")
    public <A, G extends GpuResource> GpuUploader<A, G> get(Class<A> assetType) {
        return (GpuUploader<A, G>) uploaders.get(assetType);
    }
}