package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.gpu.upload.UploadQueue;
import net.grongubbe.tengoku.client.render.frame.DrawCommandExtractor;
import net.grongubbe.tengoku.client.render.frame.LightExtractor;
import net.grongubbe.tengoku.client.render.frame.RenderFrame;
import net.grongubbe.tengoku.client.render.frame.RenderView;
import net.grongubbe.tengoku.client.scene.ComponentView;
import net.grongubbe.tengoku.client.scene.World;
import net.grongubbe.tengoku.client.scene.camera.Camera;
import net.grongubbe.tengoku.client.scene.camera.Frustum;
import net.grongubbe.tengoku.client.scene.components.*;

public final class RenderSystem {
    private final UploadQueue uploadQueue;
    private final DrawCommandExtractor extractor;
    private final LightExtractor lightExtractor;
    private final Renderer renderer;

    public RenderSystem(UploadQueue uploadQueue, DrawCommandExtractor extractor, LightExtractor lightExtractor, Renderer renderer) {
        this.uploadQueue = uploadQueue;
        this.extractor = extractor;
        this.lightExtractor = lightExtractor;
        this.renderer = renderer;
    }

    public void render(World world) {
        RenderThread.assertCurrent();

        uploadQueue.process();

        RenderFrame frame = new RenderFrame();

        ComponentView cameraView = world.query(CameraComponent.class, TransformComponent.class).findFirst().orElseThrow(() ->
                new IllegalStateException("No camera found in world")
        );

        Camera camera = cameraView.get(CameraComponent.class).camera();
        TransformComponent cameraTransform = cameraView.get(TransformComponent.class);

        frame.addView(new RenderView(camera, cameraTransform));

        world.query(LightComponent.class, TransformComponent.class).forEach(view -> {
                    LightComponent light = view.get(LightComponent.class);
                    TransformComponent transform = view.get(TransformComponent.class);

                    lightExtractor.extract(frame, transform, light);
        });

        Frustum frustum = camera.frustum(cameraTransform);

        world.query(TransformComponent.class, MeshRendererComponent.class, BoundsComponent.class).forEach(view -> {
            TransformComponent transform = view.get(TransformComponent.class);
            BoundsComponent boundsComponent = view.get(BoundsComponent.class);

            if (!frustum.intersects(boundsComponent.worldVolume(transform))) {
                return;
            }

            MeshRendererComponent renderer = view.get(MeshRendererComponent.class);

            extractor.extract(frame, transform, renderer);
        });

        renderer.render(frame);
    }
}