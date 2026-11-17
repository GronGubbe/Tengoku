package net.grongubbe.tengoku.client.render;

import net.grongubbe.tengoku.client.asset.assets.MeshAsset;
import net.grongubbe.tengoku.client.asset.assets.ShaderAsset;
import net.grongubbe.tengoku.client.render.gpu.GpuMesh;
import net.grongubbe.tengoku.client.render.gpu.GpuShader;
import net.grongubbe.tengoku.client.render.gpu.Uniforms;
import net.grongubbe.tengoku.common.io.FileIO;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;

import java.io.IOException;

import static org.lwjgl.opengl.GL15C.*;

public class Renderer implements AutoCloseable {
    private final GpuMesh quad;
    private final GpuShader shader;

    public Renderer() {
        GL.createCapabilities();
//        glEnable(GL_DEPTH_TEST);
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

        MeshAsset quadMesh = MeshAsset.builder()
                .positions(new float[]{
                        -0.5f, -0.5f, 0.0f,
                        0.5f, -0.5f, 0.0f,
                        0.5f,  0.5f, 0.0f,
                        -0.5f,  0.5f, 0.0f
                })
                .indices(new int[]{
                        0, 1, 2,
                        2, 3, 0
                })
                .primitiveMode(MeshAsset.PrimitiveMode.TRIANGLES)
                .build();

        quad = GpuMesh.fromMeshAsset(quadMesh);
    }

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        shader.bind();
        quad.bind();

        Matrix4f testModel = new Matrix4f();
        testModel.translation(0.0f, 0.0f, 0.0f);
        testModel.scale(1.0f, 1.0f, 1.0f);
        testModel.rotateXYZ(0.0f, 0.0f, 0.0f);

        shader.setUniform(Uniforms.MODEL, testModel);

        if (quad.hasIndices()) {
            glDrawElements(quad.getPrimitiveMode(), quad.getIndexCount(), GL_UNSIGNED_INT, 0L);
        } else {
            glDrawArrays(quad.getPrimitiveMode(), 0, quad.getVertexCount());
        }

        quad.unbind();
        shader.unbind();
    }

    public void resize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void close() {
        quad.unbind();
        shader.unbind();

        quad.close();
        shader.close();
    }
}

