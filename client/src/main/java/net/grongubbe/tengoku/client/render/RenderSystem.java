package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.gpu.upload.UploadQueue;
import net.grongubbe.tengoku.client.render.frame.DrawCommandExtractor;
import net.grongubbe.tengoku.client.render.frame.RenderFrame;
import net.grongubbe.tengoku.client.render.frame.RenderView;
import net.grongubbe.tengoku.client.scene.RenderObject;
import net.grongubbe.tengoku.client.scene.RenderScene;
import net.grongubbe.tengoku.client.scene.camera.Camera;

public final class RenderSystem {
    private final UploadQueue uploadQueue;
    private final DrawCommandExtractor extractor;
    private final Renderer renderer;

    public RenderSystem(UploadQueue uploadQueue, DrawCommandExtractor extractor, Renderer renderer) {
        this.uploadQueue = uploadQueue;
        this.extractor = extractor;
        this.renderer = renderer;
    }

    public void render(RenderScene scene, Camera camera) {
        RenderThread.assertCurrent();

        uploadQueue.process();

        RenderFrame frame = new RenderFrame();

        frame.addView(new RenderView(camera));

        for (RenderObject object : scene.objects()) {
            extractor.extract(frame, object);
        }

        renderer.render(frame);
    }
}