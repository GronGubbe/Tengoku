package net.grongubbe.tengoku.client.bootstrap;

import net.grongubbe.tengoku.client.asset.AssetRuntime;
import net.grongubbe.tengoku.client.asset.model.Model;
import net.grongubbe.tengoku.client.asset.model.ModelKey;
import net.grongubbe.tengoku.client.render.RenderRuntime;
import net.grongubbe.tengoku.client.render.Window;
import net.grongubbe.tengoku.client.scene.Entity;
import net.grongubbe.tengoku.client.scene.World;
import net.grongubbe.tengoku.client.scene.camera.Camera;
import net.grongubbe.tengoku.client.scene.camera.projection.PerspectiveProjection;
import net.grongubbe.tengoku.client.scene.components.*;
import net.grongubbe.tengoku.client.scene.light.DirectionalLight;
import net.grongubbe.tengoku.client.scene.light.PointLight;
import net.grongubbe.tengoku.client.test.TestCameraController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.nio.file.Path;
import java.util.Objects;

public final class GameRuntime implements Lifecycle {
    private static final Logger LOGGER = LogManager.getLogger(GameRuntime.class);

    private final PlatformRuntime platform;
    private final AssetRuntime assets;
    private final RenderRuntime render;

    private World world;

    private TransformComponent cameraTransform;
    private TestCameraController cameraController;

    private TransformComponent suzanneTransform;
    private float animationTime;

    public GameRuntime(PlatformRuntime platform, AssetRuntime assets, RenderRuntime render) {
        this.platform = Objects.requireNonNull(platform, "platform");
        this.assets = Objects.requireNonNull(assets, "assets");
        this.render = Objects.requireNonNull(render, "render");
    }

    @Override
    public void start() {
        if (world != null) {
            throw new IllegalStateException("Game runtime has already started");
        }

        Window window = platform.window();

        world = new World();

        Camera camera = new Camera(
                new PerspectiveProjection(
                        (float) Math.toRadians(50.0f),
                        (float) window.framebufferWidth() / window.framebufferHeight(),
                        0.1f,
                        1000.0f
                )
        );

        window.setResizeCamera(camera);

        Entity cameraEntity = world.createEntity();

        cameraTransform = new TransformComponent();
        cameraTransform.setPosition(0.0f, 4.0f, 10.0f);

        cameraController = new TestCameraController(cameraTransform, window);

        world.add(cameraEntity, cameraTransform);
        world.add(cameraEntity, new CameraComponent(camera));

        createTestScene();
    }

    @Override
    public void stop() {
        if (world == null) {
            return;
        }

        LOGGER.info("Cleaning up game resources");

        for (Entity entity : world.entities()) {
            world.destroy(entity);
        }

        world = null;
        cameraTransform = null;
        cameraController = null;
        suzanneTransform = null;
        animationTime = 0.0f;
    }

    public void update(float deltaTime) {
        requireStarted();

        cameraController.update(deltaTime);

        animationTime += deltaTime;

        float x = (float) Math.sin(animationTime / 1.5f) * 5.0f;
        suzanneTransform.setPosition(x, 0.5f, 0.0f);
    }

    public void render(int framebufferWidth, int framebufferHeight) {
        requireStarted();

        render.renderer().render(world, framebufferWidth, framebufferHeight);
    }

    private void createTestScene() {
        Model suzanne = assets.assets().get(new ModelKey(Path.of("models/suzanne.model.json")));
        Model cube = assets.assets().get(new ModelKey(Path.of("models/cube.model.json")));

        createGround(cube);
        createSuzanne(suzanne);
        createShadowCaster(cube);

        createSun();

//        createPointLight(new Vector3f(-5.0f, 1.0f, 0.0f), new Vector3f(1.0f, 0.15f, 0.05f));
//        createPointLight(new Vector3f(5.0f, 1.0f, 0.0f), new Vector3f(0.1f, 0.3f, 1.0f));
    }

    private void createGround(Model cube) {
        Entity entity = world.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.setPosition(0.0f, -1.5f, 0.0f);
        transform.setScale(8.0f, 0.5f, 8.0f);

        world.add(entity, transform);
        world.add(entity, new MeshRendererComponent(cube));
        world.add(entity, new BoundsComponent(cube.bounds()));
    }

    private void createSuzanne(Model model) {
        Entity entity = world.createEntity();

        suzanneTransform = new TransformComponent();
        suzanneTransform.setPosition(0.0f, 0.5f, 0.0f);
        suzanneTransform.setScale(1.0f);

        world.add(entity, suzanneTransform);
        world.add(entity, new MeshRendererComponent(model));
        world.add(entity, new BoundsComponent(model.bounds()));
    }

    private void createShadowCaster(Model cube) {
        Entity entity = world.createEntity();

        TransformComponent transform = new TransformComponent();
        transform.setPosition(-2.0f, 4.0f, 2.0f);
        transform.setScale(1.0f);

        world.add(entity, transform);
        world.add(entity, new MeshRendererComponent(cube));
        world.add(entity, new BoundsComponent(cube.bounds()));
    }

    private void createSun() {
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(new Vector3f(1.0f, 0.95f, 0.85f));
        sun.setIntensity(0.8f);

        Entity entity = world.createEntity();

        TransformComponent transform = new TransformComponent();

        transform.setRotation(
                new Quaternionf()
                        .rotateXYZ(
                                (float) Math.toRadians(-45.0f),
                                (float) Math.toRadians(-35.0f),
                                0.0f
                        )
        );

        world.add(entity, transform);
        world.add(entity, new LightComponent(sun));
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

    private void requireStarted() {
        if (world == null) {
            throw new IllegalStateException("Game runtime has not started");
        }
    }
}