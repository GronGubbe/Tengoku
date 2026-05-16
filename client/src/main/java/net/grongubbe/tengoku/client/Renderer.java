package net.grongubbe.tengoku.client;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private int frame = 0;
    
    public Renderer() {
        setupGL();
    }
    
    private void setupGL() {
        glEnable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void render(double alpha) {
        frame++;
        clear();

        float[] rgb = ColorCycle.getRGB(frame, 100);
        glClearColor(rgb[0], rgb[1], rgb[2], 0.0f);
    }

    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public int getFrame() {
        return frame;
    }
}