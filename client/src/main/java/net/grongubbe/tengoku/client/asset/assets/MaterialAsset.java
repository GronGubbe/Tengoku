package net.grongubbe.tengoku.client.asset.assets;

import net.grongubbe.tengoku.client.asset.IAsset;
import net.grongubbe.tengoku.client.material.AlphaMode;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MaterialAsset implements IAsset {
    private final String name;

    private final Vector4f baseColorFactor;
    private final TextureAsset baseColorTexture;
    private final int baseColorTexCoord;

    private final float metallicFactor;
    private final float roughnessFactor;
    private final TextureAsset metallicRoughnessTexture;
    private final int metallicRoughnessTexCoord;

    private final TextureAsset normalTexture;
    private final int normalTexCoord;
    private final float normalScale;

    private final TextureAsset occlusionTexture;
    private final int occlusionTexCoord;
    private final float occlusionStrength;

    private final Vector3f emissiveFactor;
    private final TextureAsset emissiveTexture;
    private final int emissiveTexCoord;

    private final AlphaMode alphaMode;
    private final float alphaCutoff;
    private final boolean doubleSided;

    private MaterialAsset(Builder builder) {
        this.name = builder.name;
        this.baseColorFactor = new Vector4f(builder.baseColorFactor);
        this.baseColorTexture = builder.baseColorTexture;
        this.baseColorTexCoord = builder.baseColorTexCoord;
        this.metallicFactor = builder.metallicFactor;
        this.roughnessFactor = builder.roughnessFactor;
        this.metallicRoughnessTexture = builder.metallicRoughnessTexture;
        this.metallicRoughnessTexCoord = builder.metallicRoughnessTexCoord;
        this.normalTexture = builder.normalTexture;
        this.normalTexCoord = builder.normalTexCoord;
        this.normalScale = builder.normalScale;
        this.occlusionTexture = builder.occlusionTexture;
        this.occlusionTexCoord = builder.occlusionTexCoord;
        this.occlusionStrength = builder.occlusionStrength;
        this.emissiveFactor = new Vector3f(builder.emissiveFactor);
        this.emissiveTexture = builder.emissiveTexture;
        this.emissiveTexCoord = builder.emissiveTexCoord;
        this.alphaMode = builder.alphaMode;
        this.alphaCutoff = builder.alphaCutoff;
        this.doubleSided = builder.doubleSided;
    }

    public String getName() {
        return name;
    }

    public Vector4f getBaseColorFactor() {
        return new Vector4f(baseColorFactor);
    }

    public TextureAsset getBaseColorTexture() {
        return baseColorTexture;
    }

    public int getBaseColorTexCoord() {
        return baseColorTexCoord;
    }

    public float getMetallicFactor() {
        return metallicFactor;
    }

    public float getRoughnessFactor() {
        return roughnessFactor;
    }

    public TextureAsset getMetallicRoughnessTexture() {
        return metallicRoughnessTexture;
    }

    public int getMetallicRoughnessTexCoord() {
        return metallicRoughnessTexCoord;
    }

    public TextureAsset getNormalTexture() {
        return normalTexture;
    }

    public int getNormalTexCoord() {
        return normalTexCoord;
    }

    public float getNormalScale() {
        return normalScale;
    }

    public TextureAsset getOcclusionTexture() {
        return occlusionTexture;
    }

    public int getOcclusionTexCoord() {
        return occlusionTexCoord;
    }

    public float getOcclusionStrength() {
        return occlusionStrength;
    }

    public Vector3f getEmissiveFactor() {
        return new Vector3f(emissiveFactor);
    }

    public TextureAsset getEmissiveTexture() {
        return emissiveTexture;
    }

    public int getEmissiveTexCoord() {
        return emissiveTexCoord;
    }

    public AlphaMode getAlphaMode() {
        return alphaMode;
    }

    public float getAlphaCutoff() {
        return alphaCutoff;
    }

    public boolean isDoubleSided() {
        return doubleSided;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;

        private Vector4f baseColorFactor = new Vector4f(1.0f);
        private TextureAsset baseColorTexture;
        private int baseColorTexCoord = 0;

        private float metallicFactor = 1.0f;
        private float roughnessFactor = 1.0f;
        private TextureAsset metallicRoughnessTexture;
        private int metallicRoughnessTexCoord = 0;

        private TextureAsset normalTexture;
        private int normalTexCoord = 0;
        private float normalScale = 1.0f;

        private TextureAsset occlusionTexture;
        private int occlusionTexCoord = 0;
        private float occlusionStrength = 1.0f;

        private Vector3f emissiveFactor = new Vector3f(0.0f);
        private TextureAsset emissiveTexture;
        private int emissiveTexCoord = 0;

        private AlphaMode alphaMode = AlphaMode.OPAQUE;
        private float alphaCutoff = 0.5f;
        private boolean doubleSided = false;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder baseColorFactor(Vector4f baseColorFactor) {
            this.baseColorFactor = new Vector4f(baseColorFactor);
            return this;
        }

        public Builder baseColorTexture(TextureAsset baseColorTexture) {
            this.baseColorTexture = baseColorTexture;
            return this;
        }

        public Builder baseColorTexCoord(int baseColorTexCoord) {
            this.baseColorTexCoord = baseColorTexCoord;
            return this;
        }

        public Builder metallicFactor(float metallicFactor) {
            this.metallicFactor = metallicFactor;
            return this;
        }

        public Builder roughnessFactor(float roughnessFactor) {
            this.roughnessFactor = roughnessFactor;
            return this;
        }

        public Builder metallicRoughnessTexture(TextureAsset metallicRoughnessTexture) {
            this.metallicRoughnessTexture = metallicRoughnessTexture;
            return this;
        }

        public Builder metallicRoughnessTexCoord(int metallicRoughnessTexCoord) {
            this.metallicRoughnessTexCoord = metallicRoughnessTexCoord;
            return this;
        }

        public Builder normalTexture(TextureAsset normalTexture) {
            this.normalTexture = normalTexture;
            return this;
        }

        public Builder normalTexCoord(int normalTexCoord) {
            this.normalTexCoord = normalTexCoord;
            return this;
        }

        public Builder normalScale(float normalScale) {
            this.normalScale = normalScale;
            return this;
        }

        public Builder occlusionTexture(TextureAsset occlusionTexture) {
            this.occlusionTexture = occlusionTexture;
            return this;
        }

        public Builder occlusionTexCoord(int occlusionTexCoord) {
            this.occlusionTexCoord = occlusionTexCoord;
            return this;
        }

        public Builder occlusionStrength(float occlusionStrength) {
            this.occlusionStrength = occlusionStrength;
            return this;
        }

        public Builder emissiveFactor(Vector3f emissiveFactor) {
            this.emissiveFactor = new Vector3f(emissiveFactor);
            return this;
        }

        public Builder emissiveTexture(TextureAsset emissiveTexture) {
            this.emissiveTexture = emissiveTexture;
            return this;
        }

        public Builder emissiveTexCoord(int emissiveTexCoord) {
            this.emissiveTexCoord = emissiveTexCoord;
            return this;
        }

        public Builder alphaMode(AlphaMode alphaMode) {
            this.alphaMode = alphaMode;
            return this;
        }

        public Builder alphaCutoff(float alphaCutoff) {
            this.alphaCutoff = alphaCutoff;
            return this;
        }

        public Builder doubleSided(boolean doubleSided) {
            this.doubleSided = doubleSided;
            return this;
        }

        public MaterialAsset build() {
            return new MaterialAsset(this);
        }
    }
}
