package net.grongubbe.tengoku.client.core;

import net.grongubbe.tengoku.client.asset.model.Model;
import net.grongubbe.tengoku.client.asset.model.ModelKey;
import net.grongubbe.tengoku.client.gpu.model.GpuModel;
import net.grongubbe.tengoku.client.render.Window;
import net.grongubbe.tengoku.client.util.Time;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class Tengoku {
    private static final Logger LOGGER = LogManager.getLogger(Tengoku.class);

    private final ClientServices services;
    private final Window window;
    private final Time time;

    private final CompletableFuture<GpuModel> modelFuture;
    private GpuModel model;

    public Tengoku() {
        LOGGER.info("Creating Tengoku instance");

        window = new Window(1024, 512, "Tengoku", false, true);

        services = new ClientServices();
        services.initialize();

        time = new Time(20);

        Model asset = services.assets().get(new ModelKey(Path.of("models/triangle.model.json")));
        modelFuture = services.gpuResources().get(asset);
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

            if(model == null && modelFuture.isDone()) {
                model = modelFuture.join();
            }

            services.renderLoop().frame(model);

            window.swapBuffers();
        }

        LOGGER.info("Game loop stopped");
    }

    private void tick() {
        LOGGER.trace("Tick");
        window.setWindowTitle("Tengoku " + time.fps());
    }

    public void cleanup() {
        LOGGER.info("Cleaning up client resources");

        window.dispose();
    }
}
