package net.grongubbe.tengoku.client.asset.assets;

import net.grongubbe.tengoku.client.asset.IAsset;

import java.util.Objects;

public class TextureAsset implements IAsset {
    private final String name;

    private final byte[] pixelData;

    private final int width;
    private final int height;

    private final MagFilter magFilter;
    private final MinFilter minFilter;

    private final WrapMode horizontalWrap;
    private final WrapMode verticalWrap;

    private TextureAsset(Builder builder) {
        this.name = builder.name;

        this.pixelData = builder.pixelData.clone();

        this.width = builder.width;
        this.height = builder.height;

        this.magFilter = builder.magFilter;
        this.minFilter = builder.minFilter;

        this.horizontalWrap = builder.horizontalWrap;
        this.verticalWrap = builder.verticalWrap;
    }

    public String getName() {
        return name;
    }

    public byte[] getPixelData() {
        return pixelData.clone();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public MagFilter getMagFilter() {
        return magFilter;
    }

    public MinFilter getMinFilter() {
        return minFilter;
    }

    public WrapMode getHorizontalWrap() {
        return horizontalWrap;
    }

    public WrapMode getVerticalWrap() {
        return verticalWrap;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;

        private byte[] pixelData;

        private int width;
        private int height;

        private MagFilter magFilter = MagFilter.LINEAR;
        private MinFilter minFilter = MinFilter.LINEAR_MIPMAP_LINEAR;

        private WrapMode horizontalWrap = WrapMode.REPEAT;
        private WrapMode verticalWrap = WrapMode.REPEAT;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder pixelData(byte[] pixelData) {
            this.pixelData = Objects.requireNonNull(pixelData).clone();
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder magFilter(MagFilter magFilter) {
            this.magFilter = Objects.requireNonNull(magFilter);
            return this;
        }

        public Builder minFilter(MinFilter minFilter) {
            this.minFilter = Objects.requireNonNull(minFilter);
            return this;
        }

        public Builder horizontalWrap(WrapMode horizontalWrap) {
            this.horizontalWrap = Objects.requireNonNull(horizontalWrap);
            return this;
        }

        public Builder verticalWrap(WrapMode verticalWrap) {
            this.verticalWrap = Objects.requireNonNull(verticalWrap);
            return this;
        }

        public TextureAsset build() {
            if (pixelData == null) {
                throw new IllegalStateException("Pixel data is required");
            }

            if (width <= 0 || height <= 0) {
                throw new IllegalStateException("Width and height must be positive");
            }

            long expectedSize = (long) width * height * 4;

            if (pixelData.length != expectedSize) {
                throw new IllegalStateException("Pixel data length does not match expected size");
            }

            return new TextureAsset(this);
        }
    }

    public enum MagFilter {
        NEAREST,
        LINEAR
    }

    public enum MinFilter {
        NEAREST,
        LINEAR,
        NEAREST_MIPMAP_NEAREST,
        LINEAR_MIPMAP_NEAREST,
        NEAREST_MIPMAP_LINEAR,
        LINEAR_MIPMAP_LINEAR
    }

    public enum WrapMode {
        CLAMP_TO_EDGE,
        MIRRORED_REPEAT,
        REPEAT
    }
}
