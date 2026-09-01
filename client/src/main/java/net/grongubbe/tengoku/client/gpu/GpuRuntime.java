package net.grongubbe.tengoku.client.gpu;

import net.grongubbe.tengoku.client.asset.material.Material;
import net.grongubbe.tengoku.client.asset.mesh.Mesh;
import net.grongubbe.tengoku.client.asset.model.Model;
import net.grongubbe.tengoku.client.asset.shader.Shader;
import net.grongubbe.tengoku.client.asset.texture.Texture;
import net.grongubbe.tengoku.client.gpu.opengl.*;
import net.grongubbe.tengoku.client.gpu.upload.UploadQueue;

public final class GpuRuntime {
    private UploadQueue uploadQueue;
    private GpuResourceManager resourceManager;

    public void start() {
        if (resourceManager != null) {
            throw new IllegalStateException("GPU runtime has already started");
        }

        GpuUploaderRegistry uploaderRegistry = new GpuUploaderRegistry();
        uploaderRegistry.register(Texture.class, new OpenGLTextureUploader());
        uploaderRegistry.register(Mesh.class, new OpenGLMeshUploader());
        uploaderRegistry.register(Shader.class, new OpenGLShaderUploader());
        uploaderRegistry.register(Material.class, new OpenGLMaterialUploader());
        uploaderRegistry.register(Model.class, new OpenGLModelUploader());

        uploadQueue = new UploadQueue();
        resourceManager = new GpuResourceManager(uploadQueue, uploaderRegistry);
    }

    public void stop() {
        if (resourceManager != null) {
            resourceManager.cleanup();
        }

        resourceManager = null;
        uploadQueue = null;
    }

    public GpuResourceManager resources() {
        if (resourceManager == null) {
            throw new IllegalStateException("GPU runtime has not started");
        }

        return resourceManager;
    }

    public UploadQueue uploadQueue() {
        if (uploadQueue == null) {
            throw new IllegalStateException("GPU runtime has not started");
        }

        return uploadQueue;
    }
}