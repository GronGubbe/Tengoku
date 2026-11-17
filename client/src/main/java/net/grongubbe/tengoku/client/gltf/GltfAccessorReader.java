package net.grongubbe.tengoku.client.gltf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

class GltfAccessorReader {
    private final byte[] binaryData;

    GltfAccessorReader(byte[] binaryData) {
        this.binaryData = binaryData;
    }

    float[] readFloats(GltfAccessor accessor, GltfBufferView bufferView) {
        if (accessor.componentType() != 5126) {
            throw new UnsupportedOperationException("readFloats only reads floats");
        }

        ByteBuffer buffer = ByteBuffer.wrap(binaryData).order(ByteOrder.LITTLE_ENDIAN);

        int start = accessor.byteOffset() + bufferView.byteOffset();
        Integer byteStride = bufferView.byteStride();

        buffer.position(start);

        int componentLength = switch (accessor.type()) {
            case "SCALAR" -> 1;
            case "VEC2" -> 2;
            case "VEC3" -> 3;
            case "VEC4" -> 4;
            default -> throw new IllegalStateException("Unexpected value: " + accessor.type());
        };

        int elementSize = componentLength * Float.BYTES;

        float[] result = new float[Math.multiplyExact(accessor.count(), componentLength)];

        int resultIndex = 0;

        for (int i = 0; i < accessor.count(); i++) {
            for (int j = 0; j < componentLength; j++) {
                result[resultIndex++] = buffer.getFloat();
            }

            if (byteStride != null && i < accessor.count() - 1) {
                buffer.position(buffer.position() + byteStride - elementSize);
            }
        }

        return result;
    }

     int[] readIndices(GltfAccessor accessor, GltfBufferView bufferView) {
        if (accessor.componentType() != 5121 && accessor.componentType() != 5123 && accessor.componentType() != 5125) {
            throw new UnsupportedOperationException("readIndices only reads integers");
        }

        if (!Objects.equals(accessor.type(), "SCALAR")) {
            throw new UnsupportedOperationException("Indices must be scalar");
        }

        ByteBuffer buffer = ByteBuffer.wrap(binaryData).order(ByteOrder.LITTLE_ENDIAN);

        int start = accessor.byteOffset() + bufferView.byteOffset();

        buffer.position(start);

        int[] result = new int[accessor.count()];

        for (int i = 0; i < accessor.count(); i++) {
            result[i] = switch (accessor.componentType()) {
                case 5121 -> Byte.toUnsignedInt(buffer.get());
                case 5123 -> Short.toUnsignedInt(buffer.getShort());
                case 5125 -> Math.toIntExact(Integer.toUnsignedLong(buffer.getInt()));
                default -> throw new UnsupportedOperationException("Unsupported index component type");
            };
        }

        return result;
    }
}