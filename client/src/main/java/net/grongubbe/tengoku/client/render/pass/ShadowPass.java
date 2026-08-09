package net.grongubbe.tengoku.client.render.pass;

import net.grongubbe.tengoku.client.asset.AssetManager;
import net.grongubbe.tengoku.client.asset.shader.Shader;
import net.grongubbe.tengoku.client.asset.shader.ShaderKey;
import net.grongubbe.tengoku.client.gpu.GpuResourceManager;
import net.grongubbe.tengoku.client.gpu.opengl.DirectionalShadowMap;
import net.grongubbe.tengoku.client.gpu.opengl.OpenGLMeshBinder;
import net.grongubbe.tengoku.client.gpu.opengl.OpenGLShadowExecutor;
import net.grongubbe.tengoku.client.gpu.shader.GpuShader;
import net.grongubbe.tengoku.client.render.RenderThread;
import net.grongubbe.tengoku.client.render.frame.DrawCommand;
import net.grongubbe.tengoku.client.render.frame.RenderFrame;
import net.grongubbe.tengoku.client.render.frame.RenderLight;
import net.grongubbe.tengoku.client.render.frame.ShadowView;
import net.grongubbe.tengoku.client.scene.light.DirectionalLight;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static net.grongubbe.tengoku.client.render.RenderingConstants.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public final class ShadowPass implements RenderPass {
    private static final Path SHADOW_SHADER = Path.of("shaders/shadow.shader.json");

    private final OpenGLMeshBinder meshBinder;

    private final CompletableFuture<GpuShader> shaderFuture;

    private final Vector3f lightDirection = new Vector3f();
    private final Vector3f lightPosition = new Vector3f();
    private final Quaternionf lightRotation = new Quaternionf();

    private final Vector3f shadowCenter = new Vector3f();
    private final Vector3f lightSpaceCenter = new Vector3f();

    private final Matrix4f lightView = new Matrix4f();
    private final Matrix4f lightProjection = new Matrix4f();
    private final Matrix4f lightRotationMatrix = new Matrix4f();
    private final Matrix4f inverseLightRotationMatrix = new Matrix4f();

    private final Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);

    private DirectionalShadowMap shadowMap;
    private OpenGLShadowExecutor executor;
    private ShadowView shadowView;

    public ShadowPass(AssetManager assets, GpuResourceManager gpuResources, OpenGLMeshBinder meshBinder) {
        Objects.requireNonNull(assets, "assets");
        Objects.requireNonNull(gpuResources, "gpuResources");

        this.meshBinder = Objects.requireNonNull(meshBinder, "meshBinder");

        Shader shader = assets.get(new ShaderKey(SHADOW_SHADER));

        this.shaderFuture = gpuResources.get(shader);
    }

    @Override
    public String name() {
        return "Shadow";
    }

    @Override
    public void begin() {
        RenderThread.assertCurrent();

        initialize();

        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.framebuffer());

        glViewport(0, 0, shadowMap.size(), shadowMap.size());

        glEnable(GL_DEPTH_TEST);
        glClear(GL_DEPTH_BUFFER_BIT);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);

        executor.begin();
    }

    @Override
    public void execute(RenderFrame frame) {
        RenderThread.assertCurrent();

        DirectionalLight directionalLight = null;
        RenderLight directionalRenderLight = null;

        for (RenderLight renderLight : frame.lights()) {
            if (renderLight.light() instanceof DirectionalLight light) {
                directionalLight = light;
                directionalRenderLight = renderLight;
                break;
            }
        }

        if (directionalLight == null) {
            shadowView = null;
            return;
        }

        directionalRenderLight.rotation(lightRotation);

        lightDirection.set(0.0f, 0.0f, -1.0f).rotate(lightRotation).normalize();

        if (frame.views().isEmpty()) {
            shadowView = null;
            return;
        }

        frame.views().getFirst().position(shadowCenter);

        lightRotationMatrix.identity().lookAt(
                0.0f, 0.0f, 0.0f,
                lightDirection.x, lightDirection.y, lightDirection.z,
                up.x, up.y, up.z
        );

        lightSpaceCenter.set(shadowCenter).mulPosition(lightRotationMatrix);

        float texelSize = (2.0f * DIRECTIONAL_SHADOW_SIZE) / DIRECTIONAL_SHADOW_MAP_SIZE;

        lightSpaceCenter.x = Math.round(lightSpaceCenter.x / texelSize) * texelSize;
        lightSpaceCenter.y = Math.round(lightSpaceCenter.y / texelSize) * texelSize;

        inverseLightRotationMatrix.set(lightRotationMatrix).invert();
        shadowCenter.set(lightSpaceCenter).mulPosition(inverseLightRotationMatrix);

        lightPosition.set(shadowCenter).fma(-DIRECTIONAL_SHADOW_DISTANCE, lightDirection);
        lightView.setLookAt(lightPosition, shadowCenter, up);

        lightProjection.setOrtho(
                -DIRECTIONAL_SHADOW_SIZE,
                DIRECTIONAL_SHADOW_SIZE,
                -DIRECTIONAL_SHADOW_SIZE,
                DIRECTIONAL_SHADOW_SIZE,
                DIRECTIONAL_SHADOW_NEAR,
                DIRECTIONAL_SHADOW_FAR
        );

        shadowView = new ShadowView(lightView, lightProjection);

        for (DrawCommand command : frame.commands()) {
            executor.draw(command, shadowView);
        }
    }

    @Override
    public void end() {
        RenderThread.assertCurrent();

        if (executor != null) {
            executor.end();
        }

        glCullFace(GL_BACK);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public DirectionalShadowMap shadowMap() {
        if (shadowMap == null) {
            throw new IllegalStateException("Shadow pass has not been initialized");
        }

        return shadowMap;
    }

    public ShadowView shadowView() {
        if (shadowView == null) {
            throw new IllegalStateException("Shadow pass has no directional shadow for the current frame");
        }

        return shadowView;
    }

    public boolean hasShadow() {
        return shadowView != null;
    }

    private void initialize() {
        if (executor != null) {
            return;
        }

        GpuShader shader = shaderFuture.join();

        shadowMap = new DirectionalShadowMap(DIRECTIONAL_SHADOW_MAP_SIZE);
        executor = new OpenGLShadowExecutor(shader, meshBinder);
    }
}
