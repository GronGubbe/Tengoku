package net.grongubbe.tengoku.client.graphics.shader.shaders;

import net.grongubbe.tengoku.client.graphics.shader.Shader;
import net.grongubbe.tengoku.client.graphics.shader.ShaderStage;
import net.grongubbe.tengoku.common.util.io.ResourceLoader;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class UnlitShader extends Shader {
    public UnlitShader() {
        super(
                new ShaderStage(GL_VERTEX_SHADER, loadVertexSource()),
                new ShaderStage(GL_FRAGMENT_SHADER, loadFragmentSource())
        );
    }

    private static String loadVertexSource() {
        try {
            return ResourceLoader.readString("shaders/unlit.vert");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String loadFragmentSource() {
        try {
            return ResourceLoader.readString("shaders/unlit.frag");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
