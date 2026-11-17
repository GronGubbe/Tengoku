package net.grongubbe.tengoku.client.gltf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

class GlbReader {
    private static final int GLB_MAGIC = 0x46546C67;

    private static final int JSON_CHUNK = 0x4E4F534A;
    private static final int BIN_CHUNK = 0x004E4942;

    GlbData read(byte[] glbBytes) throws IOException {
        Objects.requireNonNull(glbBytes, "glbBytes");

        if (glbBytes.length < 12) {
            throw new IOException("GLB file is too small");
        }

        ByteBuffer buffer = ByteBuffer.wrap(glbBytes).order(ByteOrder.LITTLE_ENDIAN);

        int magic = buffer.getInt();
        int version = buffer.getInt();
        long fileLength = Integer.toUnsignedLong(buffer.getInt());

        if (magic != GLB_MAGIC) {
            throw new IOException("Invalid GLB magic");
        }

        if (version != 2) {
            throw new IOException("Unsupported GLB version: " + version);
        }

        if (fileLength != glbBytes.length) {
            throw new IOException("GLB file length does not match header");
        }

        String json = null;
        byte[] binaryData = new byte[0];

        boolean binaryChunkFound = false;
        int chunkIndex = 0;

        while (buffer.hasRemaining()) {
            if (buffer.remaining() < 8) {
                throw new IOException("Incomplete GLB chunk header");
            }

            long chunkLengthLong = Integer.toUnsignedLong(buffer.getInt());
            int chunkType = buffer.getInt();

            if (chunkLengthLong > buffer.remaining()) {
                throw new IOException("GLB chunk exceeds file boundaries");
            }

            int chunkLength = (int) chunkLengthLong;

            if (chunkLength % 4 != 0) {
                throw new IOException("GLB chunk length is not 4-byte aligned");
            }

            if (chunkIndex == 0 && chunkType != JSON_CHUNK) {
                throw new IOException("First GLB chunk must be JSON");
            }

            if (chunkType == JSON_CHUNK) {
                if (chunkIndex != 0 || json != null) {
                    throw new IOException("Unexpected or duplicate JSON chunk");
                }

                byte[] jsonBytes = new byte[chunkLength];
                buffer.get(jsonBytes);

                json = new String(jsonBytes, StandardCharsets.UTF_8);

            } else if (chunkType == BIN_CHUNK) {
                if (binaryChunkFound) {
                    throw new IOException("Unexpected or duplicate BIN chunk");
                }

                binaryData = new byte[chunkLength];
                buffer.get(binaryData);

                binaryChunkFound = true;

            } else {
                buffer.position(buffer.position() + chunkLength);
            }

            chunkIndex++;
        }

        if (json == null) {
            throw new IOException("GLB file does not contain a JSON chunk");
        }

        return new GlbData(json, binaryData);
    }
}
