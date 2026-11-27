package net.grongubbe.tengoku.client;

import net.grongubbe.tengoku.client.asset.assets.ModelAsset;
import net.grongubbe.tengoku.client.ecs.Components.ModelComponent;
import net.grongubbe.tengoku.client.ecs.Components.TransformComponent;
import net.grongubbe.tengoku.client.ecs.Entity;
import net.grongubbe.tengoku.client.ecs.World;
import net.grongubbe.tengoku.client.gltf.GlbLoader;
import net.grongubbe.tengoku.client.options.Options;
import net.grongubbe.tengoku.client.options.OptionsManager;
import net.grongubbe.tengoku.client.platform.GLFWContext;
import net.grongubbe.tengoku.client.platform.Window;
import net.grongubbe.tengoku.client.render.Camera;
import net.grongubbe.tengoku.client.render.RenderExtractor;
import net.grongubbe.tengoku.client.render.RenderInstance;
import net.grongubbe.tengoku.client.render.Renderer;
import net.grongubbe.tengoku.common.io.AppPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Tengoku implements Runnable, AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(Tengoku.class);

    private final OptionsManager optionsManager;
    private final Time time;
    private final GLFWContext glfw;
    private final Window window;
    private final Renderer renderer;
    private final World world;
    private final RenderExtractor renderExtractor;
    private final GlbLoader glbLoader;

    private final Camera camera;

    private boolean initialized = false;

    public Tengoku() {
        Path appDirectory = AppPaths.getAppDirectory();

        try {
            Files.createDirectories(appDirectory);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create application directory: " + appDirectory, e);
        }

        optionsManager = new OptionsManager(appDirectory.resolve("options.json"));

        time = new Time(Options.TPS.getValue());
        glfw = new GLFWContext();
        window = new Window("Tengoku", Options.WINDOW_WIDTH.getValue(), Options.WINDOW_HEIGHT.getValue(), Options.VSYNC.getValue());
        renderer = new Renderer();
        window.setFramebufferSizeCallback((_, width, height) -> renderer.resize(width, height));
        renderer.resize(window.getFramebufferWidth(), window.getFramebufferHeight());
        world = new World();
        renderExtractor = new RenderExtractor();
        glbLoader = new GlbLoader();

        ModelAsset modelAsset;

        try {
            modelAsset = glbLoader.load("models/cube.glb");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Entity entity1 = world.createEntity();

        TransformComponent transform1 = new TransformComponent();

        transform1.setPosition(0.0f, 0.25f, 0.0f);
        transform1.setScale(0.25f);

        transform1.setRotation(new Quaternionf().rotateXYZ(
                (float) Math.toRadians(20),
                (float) Math.toRadians(30),
                0.0f
        ));

        world.addComponent(entity1, transform1);
        world.addComponent(entity1, new ModelComponent(modelAsset));

        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.rotateXYZ(0.0f, 0.0f, 0.0f);
        viewMatrix.translation(0.0f, 0.25f, 1.0f);
        viewMatrix.invert();

        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.perspective((float) Math.toRadians(90), (float) window.getFramebufferWidth() / window.getFramebufferHeight(), 0.1f, 100);

        this.camera = new Camera(viewMatrix, projectionMatrix);
    }

    public void init() {
        initialized = true;
    }

    @Override
    public void run() {
        if (!initialized) {
            LOGGER.warn("Attempted to run Tengoku before it was initialized.");
            return;
        }

        while (!window.shouldClose()) {
            time.startFrame();

            while (time.shouldTick()) {
//                update(time.getFixedDeltaTime());   // Update

                world.getComponent(world.getEntity(0), TransformComponent.class).rotate(
                        new Quaternionf().rotationY((float) (1.0f * time.getFixedDeltaTime()))
                );

                time.completeTick();
            }

            List<RenderInstance> renderInstances = renderExtractor.extract(world);
            renderer.render(renderInstances, camera);

            window.swapBuffers();
            glfw.pollEvents();

            time.endFrame();
        }
    }

    @Override
    public void close() {
        LOGGER.debug("Closing Tengoku");

        initialized = false;

        renderExtractor.close();
        renderer.close();
        window.close();
        glfw.close();
    }
}
