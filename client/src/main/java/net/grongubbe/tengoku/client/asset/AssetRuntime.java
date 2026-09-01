package net.grongubbe.tengoku.client.asset;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.grongubbe.tengoku.client.asset.cache.AssetCache;
import net.grongubbe.tengoku.client.asset.image.StbImageDecoder;
import net.grongubbe.tengoku.client.asset.material.MaterialKey;
import net.grongubbe.tengoku.client.asset.material.MaterialLoader;
import net.grongubbe.tengoku.client.asset.mesh.MeshKey;
import net.grongubbe.tengoku.client.asset.mesh.MeshLoader;
import net.grongubbe.tengoku.client.asset.mesh.importer.ObjMeshImporter;
import net.grongubbe.tengoku.client.asset.model.ModelKey;
import net.grongubbe.tengoku.client.asset.model.ModelLoader;
import net.grongubbe.tengoku.client.asset.serialization.material.MaterialDeserializer;
import net.grongubbe.tengoku.client.asset.serialization.mesh.MeshDeserializer;
import net.grongubbe.tengoku.client.asset.serialization.model.ModelDeserializer;
import net.grongubbe.tengoku.client.asset.serialization.shader.ShaderDeserializer;
import net.grongubbe.tengoku.client.asset.serialization.texture.TextureDeserializer;
import net.grongubbe.tengoku.client.asset.shader.ShaderKey;
import net.grongubbe.tengoku.client.asset.shader.ShaderLoader;
import net.grongubbe.tengoku.client.asset.texture.TextureKey;
import net.grongubbe.tengoku.client.asset.texture.TextureLoader;

public final class AssetRuntime {
    private AssetManager assetManager;

    public void start() {
        if (assetManager != null) {
            throw new IllegalStateException("Asset runtime has already started");
        }

        ObjectMapper mapper = new ObjectMapper();

        AssetLoaderRegistry loaderRegistry = new AssetLoaderRegistry();
        AssetCache assetCache = new AssetCache();

        loaderRegistry.register(TextureKey.class, new TextureLoader(new TextureDeserializer(mapper), new StbImageDecoder()));
        loaderRegistry.register(MeshKey.class, new MeshLoader(new MeshDeserializer(mapper), new ObjMeshImporter()));
        loaderRegistry.register(ShaderKey.class, new ShaderLoader(new ShaderDeserializer(mapper)));
        loaderRegistry.register(MaterialKey.class, new MaterialLoader(new MaterialDeserializer(mapper)));
        loaderRegistry.register(ModelKey.class, new ModelLoader(new ModelDeserializer(mapper)));

        assetManager = new AssetManager(assetCache, loaderRegistry);
    }

    public void stop() {
        assetManager = null;
    }

    public AssetManager assets() {
        if (assetManager == null) {
            throw new IllegalStateException("Asset runtime has not started");
        }

        return assetManager;
    }
}