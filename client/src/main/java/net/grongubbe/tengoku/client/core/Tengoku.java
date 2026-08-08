package net.grongubbe.tengoku.client.core;

import net.grongubbe.tengoku.client.asset.model.Model;
import net.grongubbe.tengoku.client.asset.model.ModelKey;
import net.grongubbe.tengoku.client.render.Window;
import net.grongubbe.tengoku.client.scene.Entity;
import net.grongubbe.tengoku.client.scene.World;
import net.grongubbe.tengoku.client.scene.camera.Camera;
import net.grongubbe.tengoku.client.scene.camera.projection.PerspectiveProjection;
import net.grongubbe.tengoku.client.scene.components.*;
import net.grongubbe.tengoku.client.scene.light.DirectionalLight;
import net.grongubbe.tengoku.client.scene.light.PointLight;
import net.grongubbe.tengoku.client.util.Time;
import net.grongubbe.tengoku.client.util.TransformUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Quaternionf;
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
        Model model = services.assets().get(new ModelKey(Path.of("models/suzanne.model.json")));

        Entity entity = world.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.setScale(2.0f);

        world.add(entity, transform);
        world.add(entity, new MeshRendererComponent(model));
        world.add(entity, new BoundsComponent(model.bounds()));

        createSun();

        createPointLight(new Vector3f(-5.0f, 1.0f, 0.0f), new Vector3f(1.0f, 0.15f, 0.05f));
        createPointLight(new Vector3f(5.0f, 1.0f, 0.0f), new Vector3f(0.1f, 0.3f, 1.0f));
    }

    private void createSun() {
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(new Vector3f(1.0f, 0.95f, 0.85f));
        sun.setIntensity(0.5f);

        Entity sunEntity = world.createEntity();

        TransformComponent sunTransform = new TransformComponent();
        sunTransform.setRotation(new Quaternionf().rotateXYZ((float) Math.toRadians(-45.0), (float) Math.toRadians(-30.0), 0.0f));

        world.add(sunEntity, sunTransform);
        world.add(sunEntity, new LightComponent(sun));
    }

    private void createPointLight(Vector3f position, Vector3f color) {
        Entity pointLightEntity = world.createEntity();

        PointLight pointLight = new PointLight();
        pointLight.setColor(color);
        pointLight.setIntensity(12.0f);
        pointLight.setRange(8.0f);

        TransformComponent pointLightTransform = new TransformComponent();
        pointLightTransform.setPosition(position.x, position.y, position.z);

        world.add(pointLightEntity, pointLightTransform);
        world.add(pointLightEntity, new LightComponent(pointLight));
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

            services.renderSystem().render(world);

            window.swapBuffers();
        }

        LOGGER.info("Game loop stopped");
    }

    private void tick() {
        LOGGER.trace("Tick");

        window.setWindowTitle("Tengoku " + time.fps());

        TransformUtils.rotateAround(cameraTransform, new Vector3f(0, 0, 0), (float) time.fixedDelta());
        TransformUtils.lookAt(cameraTransform, new Vector3f(0, 0, 0));
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