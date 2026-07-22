package net.grongubbe.tengoku.client.gpu;

import net.grongubbe.tengoku.client.gpu.upload.UploadQueue;
import net.grongubbe.tengoku.client.render.RenderThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class GpuResourceManager {
    private static final Logger LOGGER = LogManager.getLogger(GpuResourceManager.class);

    private final UploadQueue uploadQueue;
    private final GpuUploaderRegistry uploaders;

    private final Map<Object, CompletableFuture<? extends GpuResource>> cache = new ConcurrentHashMap<>();

    public GpuResourceManager(UploadQueue uploadQueue, GpuUploaderRegistry uploaders) {
        this.uploadQueue = uploadQueue;
        this.uploaders = uploaders;
    }

    @SuppressWarnings("unchecked")
    public <A,G extends GpuResource>
    CompletableFuture<G> get(A asset) {
        CompletableFuture<? extends GpuResource> existing = cache.get(asset);

        if(existing != null) {
            LOGGER.debug("Returning cached GPU resource for {}", asset.getClass().getSimpleName());
            return (CompletableFuture<G>) existing;
        }

        GpuUploader<A,G> uploader = uploaders.get((Class<A>) asset.getClass());

        if(uploader == null) {
            throw new IllegalStateException("No uploader for " + asset.getClass());
        }

        CompletableFuture<G> future = new CompletableFuture<>();

        cache.put(asset,future);

        Map<Object, CompletableFuture<GpuResource>> dependencies = uploader.dependencies(asset).stream().collect(
                Collectors.toMap(dependency -> dependency, this::get)
        );

        CompletableFuture
                .allOf(dependencies.values().toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    Map<Object,GpuResource> resolved = dependencies.entrySet().stream().collect(
                            Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().join())
                    );

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

    public void cleanup() {
        RenderThread.assertCurrent();

        LOGGER.debug("Destroying {} GPU resources", cache.size());

        for (CompletableFuture<? extends GpuResource> future : cache.values()) {
            if (!future.isDone() || future.isCompletedExceptionally()) {
                continue;
            }

            future.join().destroy();
        }

        cache.clear();
    }
}