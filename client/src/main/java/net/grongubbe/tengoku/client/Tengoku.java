package net.grongubbe.tengoku.client;

public class Tengoku {
    private final Window window;
    private final Renderer renderer;
    private final Time time;

    public Tengoku() {
        window = new Window(1024, 512, "Tengoku", false, true);
        renderer = new Renderer();
        time = new Time(20);
    }

    public void run() {
        time.start();

        while (!window.shouldClose()) {
            window.poolEvents();
            time.beginFrame();

            while (time.shouldUpdate()) {
                tick();
                time.consumeUpdate();
            }

            renderer.render(time.alphaTime());
            window.swapBuffers();
        }

        cleanup();
    }

    private void tick() {
        window.setWindowTitle("Tengoku " + time.fps());
    }

    private void cleanup() {

    }
}