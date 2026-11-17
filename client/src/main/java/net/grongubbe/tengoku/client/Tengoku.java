package net.grongubbe.tengoku.client;

import net.grongubbe.tengoku.client.ecs.World;
import net.grongubbe.tengoku.client.options.Options;
import net.grongubbe.tengoku.client.options.OptionsManager;
import net.grongubbe.tengoku.client.platform.GLFWContext;
import net.grongubbe.tengoku.client.platform.Window;
import net.grongubbe.tengoku.client.render.Renderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tengoku implements Runnable, AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(Tengoku.class);

    private final OptionsManager optionsManager;
    private final Time time;
    private final GLFWContext glfw;
    private final Window window;
    private final Renderer renderer;
    private final World world;

    private boolean initialized = false;

    public Tengoku() {
        optionsManager = new OptionsManager("C:\\Users\\Test\\AppData\\Roaming\\.tengoku\\test.json");

        time = new Time(Options.TPS.getValue());
        glfw = new GLFWContext();
        window = new Window("Tengoku", Options.WINDOW_WIDTH.getValue(), Options.WINDOW_HEIGHT.getValue(), Options.VSYNC.getValue());
        renderer = new Renderer();
        window.setFramebufferSizeCallback((_, width, height) -> renderer.resize(width, height));
        renderer.resize(window.getFramebufferWidth(), window.getFramebufferHeight());
        world = new World();
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

                time.completeTick();
            }

            renderer.render();

            window.swapBuffers();
            glfw.pollEvents();

            time.endFrame();
        }
    }

    @Override
    public void close() {
        LOGGER.debug("Closing Tengoku");

        initialized = false;
        renderer.close();
        window.close();
        glfw.close();
    }
}
