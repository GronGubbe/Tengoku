package net.grongubbe.tengoku.client.render.pass;

import net.grongubbe.tengoku.client.render.frame.RenderFrame;

public interface RenderPass {
    String name();

    void begin();

    void execute(RenderFrame frame);

    void end();
}