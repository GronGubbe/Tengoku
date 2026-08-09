package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.render.frame.RenderFrame;
import net.grongubbe.tengoku.client.render.pass.LightingPass;
import net.grongubbe.tengoku.client.render.pass.RenderPass;
import net.grongubbe.tengoku.client.render.pass.ShadowPass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public final class Renderer {
    private static final Logger LOGGER = LogManager.getLogger(Renderer.class);

    private final ShadowPass shadowPass;
    private final LightingPass lightingPass;

    public Renderer(ShadowPass shadowPass, LightingPass lightingPass) {
        this.shadowPass = Objects.requireNonNull(shadowPass, "shadowPass");
        this.lightingPass = Objects.requireNonNull(lightingPass, "lightingPass");
    }

    public void render(RenderFrame frame, int framebufferWidth, int framebufferHeight) {
        RenderThread.assertCurrent();

        if (framebufferWidth <= 0 || framebufferHeight <= 0) {
            return;
        }

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        execute(shadowPass, frame);

        if (shadowPass.hasShadow()) {
            lightingPass.setShadow(shadowPass.shadowMap(), shadowPass.shadowView());
        } else {
            lightingPass.clearShadow();
        }

        lightingPass.setViewport(framebufferWidth, framebufferHeight);
        execute(lightingPass, frame);
    }

    private void execute(RenderPass pass, RenderFrame frame) {
        LOGGER.trace("Executing render pass: {}", pass.name());

        boolean started = false;
        boolean successful = false;

        try {
            pass.begin();
            started = true;

            pass.execute(frame);
            successful = true;
        } catch (RuntimeException exception) {
            LOGGER.error("Render pass failed: {}", pass.name(), exception);
        } finally {
            if (started) {
                try {
                    pass.end();
                } catch (RuntimeException exception) {
                    LOGGER.error("Render pass cleanup failed: {}", pass.name(), exception);

                    successful = false;
                }
            }
        }

        if (successful) {
            LOGGER.trace("Completed render pass: {}", pass.name());
        }
    }
}
