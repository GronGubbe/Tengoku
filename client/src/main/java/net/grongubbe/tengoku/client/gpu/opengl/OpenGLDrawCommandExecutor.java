package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.gpu.shader.ShaderUniforms;
import net.grongubbe.tengoku.client.render.RenderThread;
import net.grongubbe.tengoku.client.render.frame.DrawCommand;
import net.grongubbe.tengoku.client.render.frame.RenderFrame;
import net.grongubbe.tengoku.client.render.frame.RenderLight;
import net.grongubbe.tengoku.client.render.frame.RenderView;
import net.grongubbe.tengoku.client.scene.light.DirectionalLight;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public final class OpenGLDrawCommandExecutor {
    private final OpenGLMaterialBinder materialBinder;
    private final OpenGLMeshBinder meshBinder;

    private final Matrix4f modelMatrix = new Matrix4f();
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f();

    private final Matrix3f normalMatrix = new Matrix3f();

    private final Vector3f cameraPosition = new Vector3f();
    private final Vector3f sunDirection = new Vector3f();
    private final Vector3f sunColor = new Vector3f();
    private float sunIntensity;

    private final Vector3f ambientColor = new Vector3f();
    private float ambientIntensity = 0.0f;

    private final Quaternionf sunRotation = new Quaternionf();

    public OpenGLDrawCommandExecutor(OpenGLMaterialBinder materialBinder, OpenGLMeshBinder meshBinder) {
        this.materialBinder = Objects.requireNonNull(materialBinder, "materialBinder");
        this.meshBinder = Objects.requireNonNull(meshBinder, "meshBinder");
    }

    public void beginView(RenderView view, RenderFrame frame, Vector3f ambientColor, float ambientIntensity) {
        RenderThread.assertCurrent();

        this.ambientColor.set(ambientColor);
        this.ambientIntensity = ambientIntensity;

        view.position(cameraPosition);

        sunDirection.set(0.0f, 0.0f, -1.0f);
        sunColor.zero();
        sunIntensity = 0.0f;

        for (RenderLight renderLight : frame.lights()) {
            if (!(renderLight.light() instanceof DirectionalLight)) {
                continue;
            }

            renderLight.rotation(sunRotation);

            sunDirection.set(0.0f, 0.0f, -1.0f).rotate(sunRotation).normalize();

            renderLight.light().color().get(sunColor);
            sunIntensity = renderLight.light().intensity();

            return;
        }
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

        normalMatrix.set(modelMatrix).normal();

        uniforms.setNormalMatrix(normalMatrix);
        uniforms.setCameraPosition(cameraPosition);

        uniforms.setSunDirection(sunDirection);
        uniforms.setSunColor(sunColor);
        uniforms.setSunIntensity(sunIntensity);

        uniforms.setAmbientColor(ambientColor);
        uniforms.setAmbientIntensity(ambientIntensity);

        meshBinder.bind(command.mesh());

        try {
            glDrawElements(GL_TRIANGLES, command.indexCount(), GL_UNSIGNED_INT, (long) command.indexOffset() * Integer.BYTES);
        } finally {
            meshBinder.unbind();
        }
    }
}