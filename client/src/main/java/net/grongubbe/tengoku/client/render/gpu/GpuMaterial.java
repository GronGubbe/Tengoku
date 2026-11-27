package net.grongubbe.tengoku.client.render.gpu;

import net.grongubbe.tengoku.client.asset.assets.MaterialAsset;
import net.grongubbe.tengoku.client.asset.assets.TextureAsset;
import net.grongubbe.tengoku.client.material.AlphaMode;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class GpuMaterial implements AutoCloseable {
    private final Vector4f baseColorFactor;
    private final GpuTexture baseColorTexture;
    private final int baseColorTexCoord;

    private final float metallicFactor;
    private final float roughnessFactor;
    private final GpuTexture metallicRoughnessTexture;
    private final int metallicRoughnessTexCoord;

    private final GpuTexture normalTexture;
    private final int normalTexCoord;
    private final float normalScale;

    private final GpuTexture occlusionTexture;
    private final int occlusionTexCoord;
    private final float occlusionStrength;

    private final Vector3f emissiveFactor;
    private final GpuTexture emissiveTexture;
    private final int emissiveTexCoord;

    private final AlphaMode alphaMode;
    private final float alphaCutoff;
    private final boolean doubleSided;

    private GpuMaterial(
            Vector4f baseColorFactor,
            GpuTexture baseColorTexture,
            int baseColorTexCoord,
            float metallicFactor,
            float roughnessFactor,
            GpuTexture metallicRoughnessTexture,
            int metallicRoughnessTexCoord,
            GpuTexture normalTexture,
            int normalTexCoord,
            float normalScale,
            GpuTexture occlusionTexture,
            int occlusionTexCoord,
            float occlusionStrength,
            Vector3f emissiveFactor,
            GpuTexture emissiveTexture,
            int emissiveTexCoord,
            AlphaMode alphaMode,
            float alphaCutoff,
            boolean doubleSided
    ) {
        this.baseColorFactor = new Vector4f(baseColorFactor);
        this.baseColorTexture = baseColorTexture;
        this.baseColorTexCoord = baseColorTexCoord;

        this.metallicFactor = metallicFactor;
        this.roughnessFactor = roughnessFactor;
        this.metallicRoughnessTexture = metallicRoughnessTexture;
        this.metallicRoughnessTexCoord = metallicRoughnessTexCoord;

        this.normalTexture = normalTexture;
        this.normalTexCoord = normalTexCoord;
        this.normalScale = normalScale;

        this.occlusionTexture = occlusionTexture;
        this.occlusionTexCoord = occlusionTexCoord;
        this.occlusionStrength = occlusionStrength;

        this.emissiveFactor = new Vector3f(emissiveFactor);
        this.emissiveTexture = emissiveTexture;
        this.emissiveTexCoord = emissiveTexCoord;

        this.alphaMode = alphaMode;
        this.alphaCutoff = alphaCutoff;
        this.doubleSided = doubleSided;
    }

    public static GpuMaterial fromMaterialAsset(MaterialAsset materialAsset) {
        GpuTexture baseColorTexture = null;
        GpuTexture metallicRoughnessTexture = null;
        GpuTexture normalTexture = null;
        GpuTexture occlusionTexture = null;
        GpuTexture emissiveTexture = null;

        try {
            baseColorTexture = createTexture(materialAsset.getBaseColorTexture());
            metallicRoughnessTexture = createTexture(materialAsset.getMetallicRoughnessTexture());
            normalTexture = createTexture(materialAsset.getNormalTexture());
            occlusionTexture = createTexture(materialAsset.getOcclusionTexture());
            emissiveTexture = createTexture(materialAsset.getEmissiveTexture());

            return new GpuMaterial(
                    materialAsset.getBaseColorFactor(),
                    baseColorTexture,
                    materialAsset.getBaseColorTexCoord(),

                    materialAsset.getMetallicFactor(),
                    materialAsset.getRoughnessFactor(),
                    metallicRoughnessTexture,
                    materialAsset.getMetallicRoughnessTexCoord(),

                    normalTexture,
                    materialAsset.getNormalTexCoord(),
                    materialAsset.getNormalScale(),

                    occlusionTexture,
                    materialAsset.getOcclusionTexCoord(),
                    materialAsset.getOcclusionStrength(),

                    materialAsset.getEmissiveFactor(),
                    emissiveTexture,
                    materialAsset.getEmissiveTexCoord(),

                    materialAsset.getAlphaMode(),
                    materialAsset.getAlphaCutoff(),
                    materialAsset.isDoubleSided()
            );
        } catch (RuntimeException | Error e) {
            closeTextures(emissiveTexture, occlusionTexture, normalTexture, metallicRoughnessTexture, baseColorTexture);

            throw e;
        }
    }

    public void bind(GpuShader shader) {
        shader.setUniform("uBaseColorFactor", baseColorFactor);
    }

    private static GpuTexture createTexture(TextureAsset textureAsset) {
        return textureAsset == null ? null : GpuTexture.fromTextureAsset(textureAsset);
    }

    private static void closeTextures(GpuTexture... textures) {
        for (GpuTexture texture : textures) {
            if (texture != null) {
                texture.close();
            }
        }
    }

    @Override
    public void close() {
        closeTextures(emissiveTexture, occlusionTexture, normalTexture, metallicRoughnessTexture, baseColorTexture);
    }
}
