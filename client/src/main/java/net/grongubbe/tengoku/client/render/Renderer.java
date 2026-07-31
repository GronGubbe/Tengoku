package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.gpu.opengl.OpenGLDrawCommandExecutor;
import net.grongubbe.tengoku.client.render.frame.DrawCommand;
import net.grongubbe.tengoku.client.render.frame.RenderFrame;
import net.grongubbe.tengoku.client.render.frame.RenderView;

import static org.lwjgl.opengl.GL11.*;

public final class Renderer {
    private final OpenGLDrawCommandExecutor drawExecutor;

    public Renderer(OpenGLDrawCommandExecutor drawExecutor) {
        this.drawExecutor = drawExecutor;
    }

    public void render(RenderFrame frame) {
        RenderThread.assertCurrent();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        for (RenderView view : frame.views()) {
            beginView(view);

            for (DrawCommand command : frame.commands()) {
                drawExecutor.draw(command, view);
            }

            endView();
        }
    }

    private void beginView(RenderView view) {
        // projection/view matrices later
    }

    private void endView() {
    }
}