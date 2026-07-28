package net.grongubbe.tengoku.client.gpu.texture;

import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.render.RenderThread;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

public final class GpuTexture implements GpuResource {
    private final int id;

    public GpuTexture(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    @Override
    public void destroy() {
        RenderThread.assertCurrent();

        glDeleteTextures(id);
    }
}