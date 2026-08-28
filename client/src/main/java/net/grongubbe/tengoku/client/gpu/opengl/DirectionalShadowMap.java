package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.render.RenderThread;

import static org.lwjgl.opengl.GL30.*;

public final class DirectionalShadowMap implements GpuResource {
    private final int framebuffer;
    private final int depthTexture;
    private final int size;

    private boolean destroyed;

    public DirectionalShadowMap(int size) {
        RenderThread.assertCurrent();

        if (size <= 0) {
            throw new IllegalArgumentException("Shadow map size must be > 0");
        }

        this.size = size;
        this.depthTexture = createDepthTexture(size);
        this.framebuffer = createFramebuffer(depthTexture);
    }

    public int framebuffer() {
        checkDestroyed();
        return framebuffer;
    }

    public int depthTexture() {
        checkDestroyed();
        return depthTexture;
    }

    public int size() {
        return size;
    }

    @Override
    public void destroy() {
        RenderThread.assertCurrent();

        if (destroyed) {
            return;
        }

        destroyed = true;

        glDeleteFramebuffers(framebuffer);
        glDeleteTextures(depthTexture);
    }

    private static int createDepthTexture(int size) {
        int texture = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, texture);

        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_DEPTH_COMPONENT,
                size,
                size,
                0,
                GL_DEPTH_COMPONENT,
                GL_FLOAT,
                0L
        );

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, new float[] {1.0f, 1.0f, 1.0f, 1.0f});
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);

        glBindTexture(GL_TEXTURE_2D, 0);

        return texture;
    }

    private static int createFramebuffer(int depthTexture) {
        int framebuffer = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);

        glFramebufferTexture2D(
                GL_FRAMEBUFFER,
                GL_DEPTH_ATTACHMENT,
                GL_TEXTURE_2D,
                depthTexture,
                0
        );

        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glDeleteFramebuffers(framebuffer);

            throw new IllegalStateException("Failed to create directional shadow framebuffer");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        return framebuffer;
    }

    private void checkDestroyed() {
        if (destroyed) {
            throw new IllegalStateException("Directional shadow map already destroyed");
        }
    }
}