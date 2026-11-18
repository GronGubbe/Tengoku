package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.asset.assets.ShaderAsset;
import net.grongubbe.tengoku.client.render.gpu.GpuMesh;
import net.grongubbe.tengoku.client.render.gpu.GpuShader;
import net.grongubbe.tengoku.client.render.gpu.Uniforms;
import net.grongubbe.tengoku.common.io.FileIO;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.util.List;

import static org.lwjgl.opengl.GL15C.*;

public class Renderer implements AutoCloseable {
    private final GpuShader shader;

    public Renderer() {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        ShaderAsset shaderAsset;

        try {
            shaderAsset = new ShaderAsset(
                    null,
                    FileIO.readString("C:\\Users\\Test\\Desktop\\Tengoku\\client\\src\\main\\resources\\shaders\\default.vert"),
                    FileIO.readString("C:\\Users\\Test\\Desktop\\Tengoku\\client\\src\\main\\resources\\shaders\\default.frag")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        shader = GpuShader.fromShaderAsset(shaderAsset);
    }

    public void render(List<RenderInstance> renderInstances, Camera camera) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.bind();

        shader.setUniform(Uniforms.VIEW, camera.viewMatrix());
        shader.setUniform(Uniforms.PROJECTION, camera.projectionMatrix());

        for (RenderInstance renderInstance : renderInstances) {
            GpuMesh gpuMesh = renderInstance.gpuMesh();

            shader.setUniform(Uniforms.MODEL, renderInstance.modelMatrix());

            gpuMesh.bind();

            if (gpuMesh.hasIndices()) {
                glDrawElements(gpuMesh.getPrimitiveMode(), gpuMesh.getIndexCount(), GL_UNSIGNED_INT, 0L);
            } else {
                glDrawArrays(gpuMesh.getPrimitiveMode(), 0, gpuMesh.getVertexCount());
            }

            gpuMesh.unbind();
        }

        shader.unbind();
    }

    public void resize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void close() {
        shader.unbind();

        shader.close();
    }
}

