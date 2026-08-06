package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.gpu.upload.UploadQueue;
import net.grongubbe.tengoku.client.render.frame.DrawCommandExtractor;
import net.grongubbe.tengoku.client.render.frame.RenderFrame;
import net.grongubbe.tengoku.client.render.frame.RenderView;
import net.grongubbe.tengoku.client.scene.World;
import net.grongubbe.tengoku.client.scene.camera.Camera;
import net.grongubbe.tengoku.client.scene.components.BoundsComponent;
import net.grongubbe.tengoku.client.scene.components.CameraComponent;
import net.grongubbe.tengoku.client.scene.components.MeshRendererComponent;
import net.grongubbe.tengoku.client.scene.components.TransformComponent;

public final class RenderSystem {
    private final UploadQueue uploadQueue;
    private final DrawCommandExtractor extractor;
    private final Renderer renderer;

    public RenderSystem(UploadQueue uploadQueue, DrawCommandExtractor extractor, Renderer renderer) {
        this.uploadQueue = uploadQueue;
        this.extractor = extractor;
        this.renderer = renderer;
    }

    public void render(World world) {
        RenderThread.assertCurrent();

        uploadQueue.process();

        RenderFrame frame = new RenderFrame();

        Camera camera = world.query(CameraComponent.class)
                .findFirst()
                .map(view -> view.get(CameraComponent.class).camera())
                .orElseThrow(() -> new IllegalStateException("No camera found in world"));

        frame.addView(new RenderView(camera));

        world.query(TransformComponent.class, MeshRendererComponent.class, BoundsComponent.class).forEach(view -> {
            TransformComponent transform = view.get(TransformComponent.class);
            BoundsComponent boundsComponent = view.get(BoundsComponent.class);

            if (!camera.frustum().intersects(boundsComponent.worldVolume(transform))) {
                return;
            }

            MeshRendererComponent renderer = view.get(MeshRendererComponent.class);

            extractor.extract(frame, transform, renderer);
        });

        renderer.render(frame);
    }
}