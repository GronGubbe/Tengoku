package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.gpu.shader.ShaderUniforms;
import net.grongubbe.tengoku.client.render.RenderThread;
import net.grongubbe.tengoku.client.render.frame.DrawCommand;
import net.grongubbe.tengoku.client.render.frame.RenderView;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

public final class OpenGLDrawCommandExecutor {
    private final OpenGLMaterialBinder materialBinder;
    private final OpenGLMeshBinder meshBinder;

    private final Matrix4f modelMatrix = new Matrix4f();
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f();

    public OpenGLDrawCommandExecutor(OpenGLMaterialBinder materialBinder, OpenGLMeshBinder meshBinder) {
        this.materialBinder = materialBinder;
        this.meshBinder = meshBinder;
    }

    public void draw(DrawCommand command, RenderView view) {
        RenderThread.assertCurrent();

        ShaderUniforms uniforms = command.material().shader().uniforms();

        materialBinder.bind(command.material());

        command.modelMatrix(modelMatrix);
        uniforms.setModel(modelMatrix);

        view.view(viewMatrix);
        uniforms.setView(viewMatrix);

        view.projection(projectionMatrix);
        uniforms.setProjection(projectionMatrix);

        meshBinder.bind(command.mesh());

        try {
            glDrawElements(GL_TRIANGLES, command.indexCount(), GL_UNSIGNED_INT, (long) command.indexOffset() * Integer.BYTES);
        } finally {
            meshBinder.unbind();
        }
    }
}