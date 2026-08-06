package net.grongubbe.tengoku.client.core;

import net.grongubbe.tengoku.client.asset.model.Model;
import net.grongubbe.tengoku.client.asset.model.ModelKey;
import net.grongubbe.tengoku.client.render.Window;
import net.grongubbe.tengoku.client.scene.Entity;
import net.grongubbe.tengoku.client.scene.World;
import net.grongubbe.tengoku.client.scene.camera.Camera;
import net.grongubbe.tengoku.client.scene.camera.projection.PerspectiveProjection;
import net.grongubbe.tengoku.client.scene.components.CameraComponent;
import net.grongubbe.tengoku.client.scene.components.MeshRendererComponent;
import net.grongubbe.tengoku.client.scene.components.TransformComponent;
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

    private final World world;

    private final TransformComponent cubeTransform;

    public Tengoku() {
        LOGGER.info("Creating Tengoku instance");

        window = new Window(1024, 512, "Tengoku", false, true);

        services = new ClientServices();
        services.initialize();

        time = new Time(20);

        Camera camera = new Camera(
                new PerspectiveProjection(
                        (float) Math.toRadians(70.0),
                        (float) window.framebufferWidth() / window.framebufferHeight(),
                        0.1f, 1000.0f
                )
        );

        camera.transform().setPosition(0, 0, 5);
        window.setResizeCamera(camera);

        world = new World();

        Entity cameraEntity = world.createEntity();

        world.add(cameraEntity, new TransformComponent());
        world.add(cameraEntity, new CameraComponent(camera));

        Model model = services.assets().get(new ModelKey(Path.of("models/cube.model.json")));
        Entity cube = world.createEntity();

        cubeTransform = new TransformComponent();

        world.add(cube, cubeTransform);
        world.add(cube, new MeshRendererComponent(model));
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

            cubeTransform.rotate(new Quaternionf().setAngleAxis(Math.toRadians(1), 0, 1, 0));

            services.renderSystem().render(world);

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

        for (Entity entity : world.entities()) {
            world.destroy(entity);
        }

        services.gpuResources().cleanup();

        window.close();
    }
}