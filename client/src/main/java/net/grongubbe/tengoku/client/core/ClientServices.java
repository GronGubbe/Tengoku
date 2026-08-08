package net.grongubbe.tengoku.client.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.grongubbe.tengoku.client.asset.AssetLoaderRegistry;
import net.grongubbe.tengoku.client.asset.AssetManager;
import net.grongubbe.tengoku.client.asset.cache.AssetCache;
import net.grongubbe.tengoku.client.asset.image.StbImageDecoder;
import net.grongubbe.tengoku.client.asset.material.Material;
import net.grongubbe.tengoku.client.asset.material.MaterialKey;
import net.grongubbe.tengoku.client.asset.material.MaterialLoader;
import net.grongubbe.tengoku.client.asset.mesh.Mesh;
import net.grongubbe.tengoku.client.asset.mesh.MeshKey;
import net.grongubbe.tengoku.client.asset.mesh.MeshLoader;
import net.grongubbe.tengoku.client.asset.mesh.importer.ObjMeshImporter;
import net.grongubbe.tengoku.client.asset.model.Model;
import net.grongubbe.tengoku.client.asset.model.ModelKey;
import net.grongubbe.tengoku.client.asset.model.ModelLoader;
import net.grongubbe.tengoku.client.asset.serialization.material.MaterialDeserializer;
import net.grongubbe.tengoku.client.asset.serialization.mesh.MeshDeserializer;
import net.grongubbe.tengoku.client.asset.serialization.model.ModelDeserializer;
import net.grongubbe.tengoku.client.asset.serialization.shader.ShaderDeserializer;
import net.grongubbe.tengoku.client.asset.serialization.texture.TextureDeserializer;
import net.grongubbe.tengoku.client.asset.shader.Shader;
import net.grongubbe.tengoku.client.asset.shader.ShaderKey;
import net.grongubbe.tengoku.client.asset.shader.ShaderLoader;
import net.grongubbe.tengoku.client.asset.texture.Texture;
import net.grongubbe.tengoku.client.asset.texture.TextureKey;
import net.grongubbe.tengoku.client.asset.texture.TextureLoader;
import net.grongubbe.tengoku.client.gpu.GpuResourceManager;
import net.grongubbe.tengoku.client.gpu.GpuUploaderRegistry;
import net.grongubbe.tengoku.client.gpu.opengl.*;
import net.grongubbe.tengoku.client.gpu.upload.UploadQueue;
import net.grongubbe.tengoku.client.render.RenderSystem;
import net.grongubbe.tengoku.client.render.Renderer;
import net.grongubbe.tengoku.client.render.frame.DrawCommandExtractor;
import net.grongubbe.tengoku.client.render.frame.LightExtractor;

public final class ClientServices {
    private final AssetLoaderRegistry assetLoaderRegistry;
    private final GpuUploaderRegistry gpuUploaderRegistry;

    private final GpuResourceManager gpuResourceManager;
    private final AssetManager assetManager;

    private final RenderSystem renderSystem;

    public ClientServices() {
        this.assetLoaderRegistry = new AssetLoaderRegistry();
        this.gpuUploaderRegistry = new GpuUploaderRegistry();

        UploadQueue uploadQueue = new UploadQueue();
        AssetCache assetCache = new AssetCache();
        OpenGLMaterialBinder materialBinder = new OpenGLMaterialBinder();
        OpenGLMeshBinder meshBinder = new OpenGLMeshBinder();
        OpenGLDrawCommandExecutor drawExecutor = new OpenGLDrawCommandExecutor(materialBinder, meshBinder);
        Renderer renderer = new Renderer(drawExecutor);

        this.gpuResourceManager = new GpuResourceManager(uploadQueue, gpuUploaderRegistry);
        this.assetManager = new AssetManager(assetCache, assetLoaderRegistry);

        DrawCommandExtractor drawCommandExtractor = new DrawCommandExtractor(gpuResourceManager);
        LightExtractor lightExtractor = new LightExtractor();

        this.renderSystem = new RenderSystem(uploadQueue, drawCommandExtractor, lightExtractor, renderer);
    }

    public void initialize() {
        registerAssetLoaders();
        registerGpuUploaders();
    }

    private void registerAssetLoaders() {
        ObjectMapper mapper = new ObjectMapper();
        assetLoaderRegistry.register(TextureKey.class, new TextureLoader(new TextureDeserializer(mapper), new StbImageDecoder()));
        assetLoaderRegistry.register(MeshKey.class, new MeshLoader(new MeshDeserializer(mapper), new ObjMeshImporter()));
        assetLoaderRegistry.register(ShaderKey.class, new ShaderLoader(new ShaderDeserializer(mapper)));
        assetLoaderRegistry.register(MaterialKey.class, new MaterialLoader(new MaterialDeserializer(mapper)));
        assetLoaderRegistry.register(ModelKey.class, new ModelLoader(new ModelDeserializer(mapper)));
    }

    private void registerGpuUploaders() {
        gpuUploaderRegistry.register(Texture.class, new OpenGLTextureUploader());
        gpuUploaderRegistry.register(Mesh.class, new OpenGLMeshUploader());
        gpuUploaderRegistry.register(Shader.class, new OpenGLShaderUploader());
        gpuUploaderRegistry.register(Material.class, new OpenGLMaterialUploader());
        gpuUploaderRegistry.register(Model.class, new OpenGLModelUploader());
    }

    public GpuResourceManager gpuResources() {
        return gpuResourceManager;
    }

    public AssetManager assets() {
        return assetManager;
    }

    public RenderSystem renderSystem() {
        return renderSystem;
    }
}
