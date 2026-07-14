package net.grongubbe.tengoku.client.graphics;

import net.grongubbe.tengoku.client.asset.assets.Utils.Assets;
import net.grongubbe.tengoku.client.asset.assets.Utils.UnlitMaterialBuilder;
import net.grongubbe.tengoku.client.asset.assets.mesh.Mesh;
import net.grongubbe.tengoku.client.asset.assets.material.Material;
import net.grongubbe.tengoku.client.asset.assets.shader.Shader;
import net.grongubbe.tengoku.client.core.Tengoku;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Renderer {
    private final Assets assets;
    private int frame = 0;

    public Renderer(Tengoku tengoku) {
        assets = tengoku.assets();
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

    public void render(double ignoredAlpha) {
        frame++;
        clear();

        Material material1 = new UnlitMaterialBuilder()
                .tint(new Vector3f(1.0f, 0.0f, 0.0f))
                .opacity(1f)
                .shader(assets.shader())
                .build(assets);

        Material material2 = new UnlitMaterialBuilder()
                .tint(new Vector3f(1.0f, 0.0f, 0.0f))
                .opacity(1f)
                .shader(assets.shader())
                .build(assets);

        int[] indices = {
                0, 1, 3, 3, 1, 2
        };

        drawQuad(
                new Vector3f(-0.7f,  0.3f, 0.0f),
                new Vector3f(-0.7f, -0.7f, 0.0f),
                new Vector3f( 0.3f, -0.7f, 0.0f),
                new Vector3f( 0.3f,  0.3f, 0.0f),
                indices,
                material1
        );

        drawQuad(
                new Vector3f(-0.3f,  0.7f, 0.1f),
                new Vector3f(-0.3f, -0.3f, 0.1f),
                new Vector3f( 0.7f, -0.3f, 0.1f),
                new Vector3f( 0.7f,  0.7f, 0.1f),
                indices,
                material2
        );
    }

    @SuppressWarnings("unused")
    private void drawTriangle(Vector3f v1, Vector3f v2, Vector3f v3, Material material) {
        float[] vertices = {
                v1.x, v1.y, v1.z,
                v2.x, v2.y, v2.z,
                v3.x, v3.y, v3.z
        };

        int[] indices = {
                0, 1, 2
        };

        Mesh mesh = new Mesh(vertices, indices, material);

        draw(mesh);

        mesh.delete();
    }

    @SuppressWarnings("unused")
    private void drawQuad(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, int[] indices, Material material) {
        float[] vertices = {
                v1.x, v1.y, v1.z,
                v2.x, v2.y, v2.z,
                v3.x, v3.y, v3.z,
                v4.x, v4.y, v4.z
        };

        Mesh mesh = new Mesh(vertices, indices, material);

        draw(mesh);

        mesh.delete();
    }

    private void draw(Mesh mesh) {
        Material material = mesh.getMaterial();
        Shader shader = material.getShader();

        shader.bind();
        shader.setUniforms(material);

        glBindVertexArray(mesh.getVao());
        glDrawElements(GL_TRIANGLES, mesh.getIndicesCount(), GL_UNSIGNED_INT, 0);

        shader.unbind();
    }

    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @SuppressWarnings("unused")
    public int getFrame() {
        return frame;
    }
}