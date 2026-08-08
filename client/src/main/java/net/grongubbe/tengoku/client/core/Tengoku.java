package net.grongubbe.tengoku.client.core;

import net.grongubbe.tengoku.client.asset.model.Model;
import net.grongubbe.tengoku.client.asset.model.ModelKey;
import net.grongubbe.tengoku.client.render.Window;
import net.grongubbe.tengoku.client.scene.Entity;
import net.grongubbe.tengoku.client.scene.World;
import net.grongubbe.tengoku.client.scene.camera.Camera;
import net.grongubbe.tengoku.client.scene.camera.projection.PerspectiveProjection;
import net.grongubbe.tengoku.client.scene.components.*;
import net.grongubbe.tengoku.client.util.Time;
import net.grongubbe.tengoku.client.util.TransformUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3f;

import java.nio.file.Path;

public final class Tengoku implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(Tengoku.class);

    private final ClientServices services;
    private final Window window;
    private final Time time;

    private final TransformComponent cameraTransform;
    private final World world;

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

        window.setResizeCamera(camera);

        world = new World();

        Entity cameraEntity = world.createEntity();

        cameraTransform = new TransformComponent();
        cameraTransform.setPosition(0, 0, 8);

        world.add(cameraEntity, cameraTransform);
        world.add(cameraEntity, new CameraComponent(camera));

        createTestScene();
    }

    private void createTestScene() {
        Model model1 = services.assets().get(new ModelKey(Path.of("models/cube1.model.json")));
        Model model2 = services.assets().get(new ModelKey(Path.of("models/cube2.model.json")));
        Model model3 = services.assets().get(new ModelKey(Path.of("models/cube3.model.json")));

        Entity cube1 = world.createEntity();
        Entity cube2 = world.createEntity();
        Entity cube3 = world.createEntity();

        TransformComponent cube1Transform = new TransformComponent();
        cube1Transform.setPosition(-1.5f, -1.5f, 0);

        TransformComponent cube2Transform = new TransformComponent();
        cube2Transform.setPosition(1.5f, -1.5f, 0);

        TransformComponent cube3Transform = new TransformComponent();
        cube3Transform.setPosition(0f, 1.5f, 0);

        world.add(cube1, cube1Transform);
        world.add(cube1, new MeshRendererComponent(model1));
        world.add(cube1, new BoundsComponent(model1.bounds()));

        world.add(cube2, cube2Transform);
        world.add(cube2, new MeshRendererComponent(model2));
        world.add(cube2, new BoundsComponent(model2.bounds()));

        world.add(cube3, cube3Transform);
        world.add(cube3, new MeshRendererComponent(model3));
        world.add(cube3, new BoundsComponent(model3.bounds()));
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

            TransformUtils.rotateAround(cameraTransform, new Vector3f(0, 0, 0), (float) Math.toRadians(1.0));
            TransformUtils.lookAt(cameraTransform, new Vector3f(0, 0, 0));

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