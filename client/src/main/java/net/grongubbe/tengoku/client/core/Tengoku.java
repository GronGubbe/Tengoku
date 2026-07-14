package net.grongubbe.tengoku.client.core;

import net.grongubbe.tengoku.client.asset.AssetStore;
import net.grongubbe.tengoku.client.asset.assets.Utils.Assets;
import net.grongubbe.tengoku.client.graphics.Renderer;
import net.grongubbe.tengoku.client.graphics.Window;
import net.grongubbe.tengoku.client.util.Time;

public class Tengoku {
    private final Tengoku tengoku;
    private final Window window;
    private final Renderer renderer;
    private final Time time;
    private final AssetStore assetStore;
    private final Assets assets;

    public Tengoku() {
        tengoku = this;
        window = new Window(1024, 512, "Tengoku", false, true);
        renderer = new Renderer(this);
        time = new Time(20);
        assetStore = new AssetStore();
        assets = new Assets(assetStore);
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

    public AssetStore assetStore() {
        return assetStore;
    }

    public Assets assets() {
        return assets;
    }

    public void cleanup() {
        window.dispose();
    }
}