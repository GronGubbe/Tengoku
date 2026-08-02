package net.grongubbe.tengoku.client.core;

import net.grongubbe.tengoku.client.asset.model.ModelKey;
import net.grongubbe.tengoku.client.render.Window;
import net.grongubbe.tengoku.client.render.scene.RenderObject;
import net.grongubbe.tengoku.client.render.scene.RenderScene;
import net.grongubbe.tengoku.client.render.scene.camera.Camera;
import net.grongubbe.tengoku.client.render.scene.camera.projection.PerspectiveProjection;
import net.grongubbe.tengoku.client.util.Time;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Quaternionf;

import java.nio.file.Path;

public final class Tengoku implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(Tengoku.class);

    private final ClientServices services;
    private final Window window;
    private final Time time;

    private final Camera camera;
    private final RenderScene scene = new RenderScene();

    public Tengoku() {
        LOGGER.info("Creating Tengoku instance");

        window = new Window(1024, 512, "Tengoku", false, true);

        camera = new Camera(new PerspectiveProjection((float) Math.toRadians(70.0), (float) window.framebufferWidth() / window.framebufferHeight(), 0.1f, 1000.0f));
        camera.transform().setPosition(0, 0, 4);

        window.setResizeCamera(camera);

        services = new ClientServices();
        services.initialize();

        time = new Time(20);

        RenderObject renderObject = new RenderObject(services.assets().get(new ModelKey(Path.of("models/cube.model.json"))));
        scene.add(renderObject);
    }

    public void run() {
        LOGGER.info("Starting game loop");

        time.start();

        while (!window.shouldClose()) {
            window.pollEvents();

            time.beginFrame();

            while (time.shouldUpdate()) {
                tick();
                time.consumeUpdate();
            }

            services.renderSystem().render(scene, camera);

            scene.objects().getFirst().transform().rotate(new Quaternionf().setAngleAxis((float) Math.toRadians(1), 0, 1, 0));

            window.swapBuffers();
        }

        LOGGER.info("Game loop stopped");
    }

    private void tick() {
        LOGGER.trace("Tick");

        window.setWindowTitle("Tengoku " + time.fps());
    }

    @Override
    public void close() {
        LOGGER.info("Cleaning up client resources");

        services.gpuResources().cleanup();

        window.close();
    }
}