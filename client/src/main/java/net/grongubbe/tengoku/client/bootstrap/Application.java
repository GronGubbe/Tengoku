package net.grongubbe.tengoku.client.bootstrap;

import net.grongubbe.tengoku.client.render.Window;
import net.grongubbe.tengoku.client.util.Time;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public final class Application implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    private final PlatformRuntime platform;
    private final GameRuntime game;

    private final LifecycleCoordinator lifecycle;

    private final Time time;

    public Application(PlatformRuntime platform, EngineRuntime engine, GameRuntime game) {
        this.platform = Objects.requireNonNull(platform, "platform");
        this.game = Objects.requireNonNull(game, "game");

        CleanupStack cleanupStack = new CleanupStack();
        lifecycle = new LifecycleCoordinator(cleanupStack);

        lifecycle.register("platform", platform);
        lifecycle.register("engine", engine);
        lifecycle.register("game", game);

        time = new Time(20);
    }

    public void run() {
        LOGGER.info("Starting application");

        lifecycle.start();
        time.start();

        Window window = platform.window();

        while (!window.shouldClose()) {
            window.pollEvents();

            time.beginFrame();

            while (time.shouldUpdate()) {
                window.updateInput();

                update();

                time.consumeUpdate();
            }

            game.render(window.framebufferWidth(), window.framebufferHeight());

            window.swapBuffers();
        }

        LOGGER.info("Application stopped");
    }

    private void update() {
        LOGGER.trace("Tick");

        platform.window().setWindowTitle("Tengoku " + time.fps());

        game.update((float) time.fixedDelta());
    }

    @Override
    public void close() {
        LOGGER.info("Cleaning up application");

        LifecycleState state = lifecycle.state();

        if (state == LifecycleState.RUNNING) {
            lifecycle.stop();
        }
    }
}