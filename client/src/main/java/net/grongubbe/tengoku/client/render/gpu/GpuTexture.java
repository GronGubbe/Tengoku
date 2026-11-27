package net.grongubbe.tengoku.client.render.gpu;

import net.grongubbe.tengoku.client.asset.assets.TextureAsset;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL14C.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;

public class GpuTexture implements AutoCloseable {
    private final int textureId;

    private GpuTexture(byte[] pixelData, int width, int height, int magFilter, int minFilter, int wrapS, int wrapT) {
        textureId = glGenTextures();

        try {
            if (textureId == 0) {
                throw new IllegalStateException("Failed to create texture");
            }

            glBindTexture(GL_TEXTURE_2D, textureId);

            ByteBuffer pixelBuffer = MemoryUtil.memAlloc(pixelData.length);

            try {
                pixelBuffer.put(pixelData).flip();
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);
            } finally {
                MemoryUtil.memFree(pixelBuffer);
            }

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapS);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapT);

            switch (minFilter) {
                case GL_NEAREST_MIPMAP_NEAREST,
                     GL_LINEAR_MIPMAP_NEAREST,
                     GL_NEAREST_MIPMAP_LINEAR,
                     GL_LINEAR_MIPMAP_LINEAR -> glGenerateMipmap(GL_TEXTURE_2D);

                case GL_NEAREST, GL_LINEAR -> {
                }

                default -> throw new IllegalArgumentException("Unknown minification filter: " + minFilter);
            }

            glBindTexture(GL_TEXTURE_2D, 0);
        } catch (RuntimeException | Error e) {
            close();
            throw e;
        }
    }

    public static GpuTexture fromTextureAsset(TextureAsset textureAsset) {
        int magFilter = switch (textureAsset.getMagFilter()) {
            case NEAREST -> GL_NEAREST;
            case LINEAR -> GL_LINEAR;
        };

        int minFilter = switch (textureAsset.getMinFilter()) {
            case NEAREST -> GL_NEAREST;
            case LINEAR -> GL_LINEAR;
            case NEAREST_MIPMAP_NEAREST -> GL_NEAREST_MIPMAP_NEAREST;
            case LINEAR_MIPMAP_NEAREST -> GL_LINEAR_MIPMAP_NEAREST;
            case NEAREST_MIPMAP_LINEAR -> GL_NEAREST_MIPMAP_LINEAR;
            case LINEAR_MIPMAP_LINEAR -> GL_LINEAR_MIPMAP_LINEAR;
        };

        int wrapS = switch (textureAsset.getHorizontalWrap()) {
            case CLAMP_TO_EDGE -> GL_CLAMP_TO_EDGE;
            case MIRRORED_REPEAT -> GL_MIRRORED_REPEAT;
            case REPEAT -> GL_REPEAT;
        };

        int wrapT = switch (textureAsset.getVerticalWrap()) {
            case CLAMP_TO_EDGE -> GL_CLAMP_TO_EDGE;
            case MIRRORED_REPEAT -> GL_MIRRORED_REPEAT;
            case REPEAT -> GL_REPEAT;
        };

        return new GpuTexture(textureAsset.getPixelData(), textureAsset.getWidth(), textureAsset.getHeight(), magFilter, minFilter, wrapS, wrapT);
    }

    public void bind(int textureUnit) {
        glActiveTexture(GL_TEXTURE0 + textureUnit);
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public void unbind(int textureUnit) {
        glActiveTexture(GL_TEXTURE0 + textureUnit);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    public void close() {
        glDeleteTextures(textureId);
    }
}
