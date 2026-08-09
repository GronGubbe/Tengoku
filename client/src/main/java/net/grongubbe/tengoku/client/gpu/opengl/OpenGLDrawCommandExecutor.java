package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.gpu.shader.ShaderUniforms;
import net.grongubbe.tengoku.client.render.RenderThread;
import net.grongubbe.tengoku.client.render.frame.*;
import net.grongubbe.tengoku.client.scene.light.DirectionalLight;
import net.grongubbe.tengoku.client.scene.light.PointLight;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;

import static net.grongubbe.tengoku.client.render.RenderingConstants.MAX_POINT_LIGHTS;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public final class OpenGLDrawCommandExecutor {
    private static final int SHADOW_TEXTURE_UNIT = 7;

    private final OpenGLMaterialBinder materialBinder;
    private final OpenGLMeshBinder meshBinder;

    private final Matrix4f modelMatrix = new Matrix4f();
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f();

    private final Matrix3f normalMatrix = new Matrix3f();

    private final Matrix4f shadowViewMatrix = new Matrix4f();
    private final Matrix4f shadowProjectionMatrix = new Matrix4f();

    private final Vector3f cameraPosition = new Vector3f();
    private final Vector3f sunDirection = new Vector3f();
    private final Vector3f sunColor = new Vector3f();
    private float sunIntensity;

    private final Vector3f ambientColor = new Vector3f();
    private float ambientIntensity;

    private final Quaternionf sunRotation = new Quaternionf();

    private final Vector3f[] pointLightPositions = new Vector3f[MAX_POINT_LIGHTS];
    private final Vector3f[] pointLightColors = new Vector3f[MAX_POINT_LIGHTS];
    private final float[] pointLightRanges = new float[MAX_POINT_LIGHTS];

    private int pointLightCount;

    public OpenGLDrawCommandExecutor(
            OpenGLMaterialBinder materialBinder,
            OpenGLMeshBinder meshBinder
    ) {
        this.materialBinder = Objects.requireNonNull(
                materialBinder,
                "materialBinder"
        );

        this.meshBinder = Objects.requireNonNull(
                meshBinder,
                "meshBinder"
        );

        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            pointLightPositions[i] = new Vector3f();
            pointLightColors[i] = new Vector3f();
        }
    }

    public void beginView(RenderView view, RenderFrame frame, Vector3f ambientColor, float ambientIntensity) {
        RenderThread.assertCurrent();

        this.ambientColor.set(ambientColor);
        this.ambientIntensity = ambientIntensity;

        view.position(cameraPosition);

        sunDirection.set(0.0f, 0.0f, -1.0f);
        sunColor.zero();
        sunIntensity = 0.0f;

        pointLightCount = 0;

        for (RenderLight renderLight : frame.lights()) {
            if (renderLight.light() instanceof DirectionalLight) {
                setSun(renderLight);
                continue;
            }

            if (renderLight.light() instanceof PointLight) {
                if (pointLightCount >= MAX_POINT_LIGHTS) {
                    throw new IllegalStateException("Exceeded maximum number of point lights: " + MAX_POINT_LIGHTS);
                }

                setPointLight(pointLightCount, renderLight);
                pointLightCount++;
            }
        }
    }

    public void draw(DrawCommand command, RenderView view, DirectionalShadowMap shadowMap, ShadowView shadowView) {
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

        uniforms.setPointLightCount(pointLightCount);

        for (int i = 0; i < pointLightCount; i++) {
            uniforms.setPointLightPosition(i, pointLightPositions[i]);
            uniforms.setPointLightColor(i, pointLightColors[i]);
            uniforms.setPointLightRange(i, pointLightRanges[i]);
        }

        if (shadowMap != null && shadowView != null) {
            bindShadow(shadowMap, shadowView, uniforms);
        }

        meshBinder.bind(command.mesh());

        try {
            glDrawElements(
                    GL_TRIANGLES,
                    command.indexCount(),
                    GL_UNSIGNED_INT,
                    (long) command.indexOffset() * Integer.BYTES
            );
        } finally {
            meshBinder.unbind();
            unbindShadow();
        }
    }

    private void bindShadow(DirectionalShadowMap shadowMap, ShadowView shadowView, ShaderUniforms uniforms) {
        glActiveTexture(GL_TEXTURE0 + SHADOW_TEXTURE_UNIT);
        glBindTexture(GL_TEXTURE_2D, shadowMap.depthTexture());

        shadowView.view(shadowViewMatrix);
        shadowView.projection(shadowProjectionMatrix);

        uniforms.setShadowMap(SHADOW_TEXTURE_UNIT);
        uniforms.setShadowView(shadowViewMatrix);
        uniforms.setShadowProjection(shadowProjectionMatrix);
    }

    private void unbindShadow() {
        glActiveTexture(GL_TEXTURE0 + SHADOW_TEXTURE_UNIT);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void setSun(RenderLight renderLight) {
        DirectionalLight light = (DirectionalLight) renderLight.light();

        renderLight.rotation(sunRotation);

        sunDirection.set(0.0f, 0.0f, -1.0f).rotate(sunRotation).normalize();

        light.color().get(sunColor);
        sunIntensity = light.intensity();
    }

    private void setPointLight(int index, RenderLight renderLight) {
        PointLight light = (PointLight) renderLight.light();

        renderLight.position(pointLightPositions[index]);

        light.color().get(pointLightColors[index]);

        pointLightColors[index].mul(light.intensity());

        pointLightRanges[index] = light.range();
    }
}