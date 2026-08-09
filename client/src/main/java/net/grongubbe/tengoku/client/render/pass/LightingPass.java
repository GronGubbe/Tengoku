package net.grongubbe.tengoku.client.render.pass;

import net.grongubbe.tengoku.client.gpu.opengl.DirectionalShadowMap;
import net.grongubbe.tengoku.client.gpu.opengl.OpenGLDrawCommandExecutor;
import net.grongubbe.tengoku.client.render.RenderThread;
import net.grongubbe.tengoku.client.render.frame.DrawCommand;
import net.grongubbe.tengoku.client.render.frame.RenderFrame;
import net.grongubbe.tengoku.client.render.frame.RenderView;
import net.grongubbe.tengoku.client.render.frame.ShadowView;

import java.util.Objects;

import static net.grongubbe.tengoku.client.render.RenderingConstants.AMBIENT_COLOR;
import static net.grongubbe.tengoku.client.render.RenderingConstants.AMBIENT_INTENSITY;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public final class LightingPass implements RenderPass {
    private final OpenGLDrawCommandExecutor drawExecutor;

    private int viewportWidth;
    private int viewportHeight;

    private DirectionalShadowMap shadowMap;
    private ShadowView shadowView;

    public LightingPass(OpenGLDrawCommandExecutor drawExecutor) {
        this.drawExecutor = Objects.requireNonNull(drawExecutor, "drawExecutor");
    }

    @Override
    public String name() {
        return "Lighting";
    }

    public void setViewport(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("Viewport width must be > 0");
        }

        if (height <= 0) {
            throw new IllegalArgumentException("Viewport height must be > 0");
        }

        this.viewportWidth = width;
        this.viewportHeight = height;
    }

    public void setShadow(DirectionalShadowMap shadowMap, ShadowView shadowView) {
        this.shadowMap = Objects.requireNonNull(shadowMap, "shadowMap");
        this.shadowView = Objects.requireNonNull(shadowView, "shadowView");
    }

    public void clearShadow() {
        shadowMap = null;
        shadowView = null;
    }

    @Override
    public void begin() {
        RenderThread.assertCurrent();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glViewport(0, 0, viewportWidth, viewportHeight);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    @Override
    public void execute(RenderFrame frame) {
        RenderThread.assertCurrent();

        for (RenderView view : frame.views()) {
            drawExecutor.beginView(view, frame, AMBIENT_COLOR, AMBIENT_INTENSITY);

            for (DrawCommand command : frame.commands()) {
                drawExecutor.draw(command, view, shadowMap, shadowView);
            }
        }
    }

    @Override
    public void end() {
        RenderThread.assertCurrent();
    }
}
