package net.grongubbe.tengoku.client.asset.image;

import net.grongubbe.tengoku.client.asset.texture.TextureFormat;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public final class ImageData implements AutoCloseable {
    private final int width;
    private final int height;
    private final TextureFormat format;
    private ByteBuffer pixels;

    public ImageData(int width, int height, TextureFormat format, ByteBuffer pixels) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.pixels = pixels;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public TextureFormat format() {
        return format;
    }

    public ByteBuffer pixels() {
        return pixels;
    }

    @Override
    public void close() {
        if (pixels != null) {
            MemoryUtil.memFree(pixels);
            pixels = null;
        }
    }
}