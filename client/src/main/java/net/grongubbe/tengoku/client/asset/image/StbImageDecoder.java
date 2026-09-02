package net.grongubbe.tengoku.client.asset.image;

import net.grongubbe.tengoku.client.asset.texture.TextureFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public final class StbImageDecoder implements ImageDecoder {
    private static final Logger LOGGER = LogManager.getLogger(StbImageDecoder.class);

    @Override
    public ImageData decode(InputStream input) throws IOException {
        STBImage.stbi_set_flip_vertically_on_load(true);
        ByteBuffer encoded = readInput(input);

        try {
            LOGGER.debug("Encoded image size {}", encoded.remaining());

            IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
            IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
            IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);

            ByteBuffer stbPixels = STBImage.stbi_load_from_memory(
                    encoded,
                    widthBuffer,
                    heightBuffer,
                    channelsBuffer,
                    0
            );

            if (stbPixels == null) {
                throw new IOException("Failed decoding image: " + STBImage.stbi_failure_reason());
            }

            try {
                int width = widthBuffer.get(0);
                int height = heightBuffer.get(0);
                int channels = channelsBuffer.get(0);

                LOGGER.debug("STB decoded {}x{} channels {}", width, height, channels);

                TextureFormat format = switch (channels) {
                    case 1 -> TextureFormat.R8;
                    case 2 -> TextureFormat.RG8;
                    case 3 -> TextureFormat.RGB8;
                    case 4 -> TextureFormat.RGBA8;
                    default -> throw new IOException("Unsupported channel count: " + channels);
                };


                int size = width * height * channels;

                ByteBuffer pixels = MemoryUtil.memAlloc(size);

                LOGGER.debug("Copying {} bytes from STB buffer (remaining {})", size, stbPixels.remaining());

                MemoryUtil.memCopy(MemoryUtil.memAddress(stbPixels), MemoryUtil.memAddress(pixels), size);

                pixels.limit(size);
                pixels.position(0);

                LOGGER.debug("Creating ImageData");

                return new ImageData(width, height, format, pixels);
            } finally {
                STBImage.stbi_image_free(stbPixels);
            }

        } finally {
            MemoryUtil.memFree(encoded);
        }
    }

    private ByteBuffer readInput(InputStream input) throws IOException {
        byte[] bytes = input.readAllBytes();

        if (bytes.length == 0) {
            throw new IOException("Image data is empty.");
        }

        ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);

        buffer.put(bytes);
        buffer.flip();

        return buffer;
    }
}