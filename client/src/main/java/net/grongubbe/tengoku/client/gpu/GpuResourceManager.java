package net.grongubbe.tengoku.client.gpu;

import net.grongubbe.tengoku.client.gpu.upload.UploadQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class GpuResourceManager {
    private static final Logger LOGGER = LogManager.getLogger(GpuResourceManager.class);

    private final UploadQueue uploadQueue;
    private final GpuUploaderRegistry uploaders;

    private final Map<Object, CompletableFuture<?>> resources = new ConcurrentHashMap<>();

    public GpuResourceManager(UploadQueue uploadQueue, GpuUploaderRegistry uploaders) {
        this.uploadQueue = uploadQueue;
        this.uploaders = uploaders;
    }

    @SuppressWarnings("unchecked")
    public <A,G extends GpuResource>
    CompletableFuture<G> get(A asset) {
        CompletableFuture<?> existing = resources.get(asset);

        if(existing != null) {
            LOGGER.debug("Returning cached GPU resource for {}", asset.getClass().getSimpleName());
            return (CompletableFuture<G>) existing;
        }

        GpuUploader<A,G> uploader = uploaders.get((Class<A>) asset.getClass());

        if(uploader == null) {
            throw new IllegalStateException("No uploader for " + asset.getClass());
        }

        CompletableFuture<G> future = new CompletableFuture<>();

        resources.put(asset,future);

        List<CompletableFuture<GpuResource>> dependencies = uploader.dependencies(asset).stream().map(this::get).toList();

        CompletableFuture
                .allOf(dependencies.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    List<GpuResource> resolved = dependencies.stream().map(CompletableFuture::join).toList();

                    LOGGER.debug("Creating GPU resource for {}", asset.getClass().getSimpleName());

                    uploadQueue.submit(() -> {
                        try {
                            G resource = uploader.upload(asset, resolved);

                            LOGGER.debug("Created GPU resource {}", resource.getClass().getSimpleName());

                            future.complete(resource);
                        } catch(Throwable throwable) {
                            LOGGER.error("GPU resource creation failed for {}", asset, throwable);

                            future.completeExceptionally(throwable);
                        }
                    });
                });
        return future;
    }
}