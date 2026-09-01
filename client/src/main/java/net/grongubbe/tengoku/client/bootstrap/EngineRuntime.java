package net.grongubbe.tengoku.client.bootstrap;

import net.grongubbe.tengoku.client.asset.AssetRuntime;
import net.grongubbe.tengoku.client.gpu.GpuRuntime;
import net.grongubbe.tengoku.client.render.RenderRuntime;

public final class EngineRuntime implements Lifecycle {
    private final AssetRuntime assets;
    private final GpuRuntime gpu;
    private final RenderRuntime render;

    public EngineRuntime() {
        assets = new AssetRuntime();
        gpu = new GpuRuntime();
        render = new RenderRuntime(assets, gpu);
    }

    @Override
    public void start() {
        assets.start();
        gpu.start();
        render.start();
    }

    @Override
    public void stop() {
        render.stop();
        gpu.stop();
        assets.stop();
    }

    public AssetRuntime assetRuntime() {
        return assets;
    }

    public RenderRuntime renderRuntime() {
        return render;
    }
}