package net.grongubbe.tengoku.client.gpu.opengl;

import net.grongubbe.tengoku.client.asset.image.ImageData;
import net.grongubbe.tengoku.client.asset.texture.Texture;
import net.grongubbe.tengoku.client.asset.texture.TextureFormat;
import net.grongubbe.tengoku.client.gpu.GpuResource;
import net.grongubbe.tengoku.client.gpu.GpuUploader;
import net.grongubbe.tengoku.client.gpu.texture.GpuTexture;
import net.grongubbe.tengoku.client.render.RenderThread;

import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public final class OpenGLTextureUploader implements GpuUploader<Texture, GpuTexture> {
    @Override
    public GpuTexture upload(Texture texture, List<GpuResource> dependencies) {
        RenderThread.assertCurrent();

        ImageData image = texture.image(); // TODO: handle image closing

        int textureId = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        TextureFormat format = image.format();

        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                internalFormat(format),
                image.width(),
                image.height(),
                0,
                format(format),
                GL_UNSIGNED_BYTE,
                image.pixels()
        );

        glGenerateMipmap(GL_TEXTURE_2D);

        glBindTexture(GL_TEXTURE_2D, 0);

        return new GpuTexture(textureId);
    }

    private int internalFormat(TextureFormat format) {
        return switch (format) {
            case R8 -> GL_R8;
            case RG8 -> GL_RG8;
            case RGB8 -> GL_RGB8;
            case RGBA8 -> GL_RGBA8;
        };
    }

    private int format(TextureFormat format) {
        return switch (format) {
            case R8 -> GL_RED;
            case RG8 -> GL_RG;
            case RGB8 -> GL_RGB;
            case RGBA8 -> GL_RGBA;
        };
    }
}