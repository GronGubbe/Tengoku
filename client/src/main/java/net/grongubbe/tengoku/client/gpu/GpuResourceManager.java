package net.grongubbe.tengoku.client.gpu;

import net.grongubbe.tengoku.client.asset.Asset;
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

    private final Map<Asset, CompletableFuture<? extends GpuResource>> cache = new ConcurrentHashMap<>();

    private boolean disposed = false;

    public GpuResourceManager(UploadQueue uploadQueue, GpuUploaderRegistry uploaders) {
        this.uploadQueue = uploadQueue;
        this.uploaders = uploaders;
    }

    @SuppressWarnings("unchecked")
    public <A extends Asset, G extends GpuResource> CompletableFuture<G> get(A asset) {
        if (disposed) {
            throw new IllegalStateException("GpuResourceManager has been disposed");
        }

        if (asset == null) {
            throw new IllegalArgumentException("GPU asset cannot be null");
        }

        CompletableFuture<? extends GpuResource> existing = cache.get(asset);

        if(existing != null) {
            LOGGER.trace("Returning cached GPU resource for {}", asset);
            return (CompletableFuture<G>) existing;
        }

        GpuUploader<A,G> uploader = uploaders.get((Class<A>) asset.getClass());

        if(uploader == null) {
            throw new IllegalStateException("No uploader for " + asset.getClass());
        }

        CompletableFuture<G> future = new CompletableFuture<>();

        cache.put(asset,future);

        Map<Asset, CompletableFuture<GpuResource>> dependencies = uploader.dependencies(asset).stream().collect(
                Collectors.toMap(dependency -> dependency, this::get)
        );

        CompletableFuture
                .allOf(dependencies.values().toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    Map<Asset, GpuResource> resolved;

                    try {
                        resolved = dependencies.entrySet().stream().collect(
                                Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().join())
                        );
                    } catch (Throwable throwable) {
                        future.completeExceptionally(
                                new IllegalStateException(
                                        """
                                        Failed to resolve GPU resource dependencies.
                    
                                        Asset:
                                        %s
                                        """.formatted(asset), throwable
                                )
                        );

                        return;
                    }

                    LOGGER.debug("Creating GPU resource for {}", asset);

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

        disposed = true;

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