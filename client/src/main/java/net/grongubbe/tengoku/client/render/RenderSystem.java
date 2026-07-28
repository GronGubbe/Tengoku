package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.gpu.upload.UploadQueue;
import net.grongubbe.tengoku.client.render.frame.RenderFrame;

public final class RenderSystem {
    private final UploadQueue uploadQueue;
    private final Renderer renderer;

    public RenderSystem(UploadQueue uploadQueue, Renderer renderer) {
        this.uploadQueue = uploadQueue;
        this.renderer = renderer;
    }

    public void render(RenderFrame frame) {
        RenderThread.assertCurrent();

        uploadQueue.process();

        renderer.render(frame);
    }
}