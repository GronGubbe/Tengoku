package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.gpu.model.GpuModel;
import net.grongubbe.tengoku.client.gpu.upload.UploadQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RenderLoop {
    private static final Logger LOGGER = LogManager.getLogger(RenderLoop.class);

    private final UploadQueue uploadQueue;
    private final Renderer renderer;

    public RenderLoop(UploadQueue uploadQueue, Renderer renderer) {
        this.uploadQueue = uploadQueue;
        this.renderer = renderer;
    }

    public void frame(GpuModel model) {
        RenderThread.assertCurrent();

        LOGGER.trace("Rendering frame");

        uploadQueue.process();

        if(model == null) {
            return;
        }

        renderer.render(model);
    }
}