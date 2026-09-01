package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.asset.AssetRuntime;
import net.grongubbe.tengoku.client.gpu.GpuRuntime;
import net.grongubbe.tengoku.client.gpu.opengl.OpenGLDrawCommandExecutor;
import net.grongubbe.tengoku.client.gpu.opengl.OpenGLMaterialBinder;
import net.grongubbe.tengoku.client.gpu.opengl.OpenGLMeshBinder;
import net.grongubbe.tengoku.client.render.frame.DrawCommandExtractor;
import net.grongubbe.tengoku.client.render.frame.LightExtractor;
import net.grongubbe.tengoku.client.render.pass.LightingPass;
import net.grongubbe.tengoku.client.render.pass.ShadowPass;

import java.util.Objects;

public final class RenderRuntime {
    private final AssetRuntime assets;
    private final GpuRuntime gpu;

    private RenderSystem renderSystem;

    public RenderRuntime(AssetRuntime assets, GpuRuntime gpu) {
        this.assets = Objects.requireNonNull(assets, "assets");
        this.gpu = Objects.requireNonNull(gpu, "gpu");
    }

    public void start() {
        if (renderSystem != null) {
            throw new IllegalStateException("Render runtime has already started");
        }

        OpenGLMaterialBinder materialBinder = new OpenGLMaterialBinder();
        OpenGLMeshBinder meshBinder = new OpenGLMeshBinder();

        OpenGLDrawCommandExecutor drawExecutor = new OpenGLDrawCommandExecutor(materialBinder, meshBinder);

        ShadowPass shadowPass = new ShadowPass(assets.assets(), gpu.resources(), meshBinder);
        LightingPass lightingPass = new LightingPass(drawExecutor);

        Renderer renderer = new Renderer(shadowPass, lightingPass);

        DrawCommandExtractor drawCommandExtractor = new DrawCommandExtractor(gpu.resources());
        LightExtractor lightExtractor = new LightExtractor();

        renderSystem = new RenderSystem(gpu.uploadQueue(), drawCommandExtractor, lightExtractor, renderer);
    }

    public void stop() {
        renderSystem = null;
    }

    public RenderSystem renderer() {
        if (renderSystem == null) {
            throw new IllegalStateException("Render runtime has not started");
        }

        return renderSystem;
    }
}