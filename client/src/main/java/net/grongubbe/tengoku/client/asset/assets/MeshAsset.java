package net.grongubbe.tengoku.client.asset.assets;

import net.grongubbe.tengoku.client.asset.IAsset;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MeshAsset implements IAsset {
    private final String name;

    private final float[] positions;
    private final float[] normals;
    private final float[] tangents;

    private final Map<Integer, float[]> textureCoordinates;

    private final float[] colors;
    private final int[] indices;

    private final PrimitiveMode primitiveMode;

    private MeshAsset(Builder builder) {
        this.name = builder.name;

        this.positions = builder.positions.clone();
        this.normals = builder.normals != null ? builder.normals.clone() : null;
        this.tangents = builder.tangents != null ? builder.tangents.clone() : null;

        Map<Integer, float[]> copy = new HashMap<>();
        builder.textureCoordinates.forEach((Integer key, float[] value) ->
                copy.put(key, value.clone())
        );
        this.textureCoordinates = Map.copyOf(copy);

        this.colors = builder.colors != null ? builder.colors.clone() : null;
        this.indices = builder.indices != null ? builder.indices.clone() : null;

        this.primitiveMode = builder.primitiveMode;
    }

    public String getName() {
        return name;
    }

    public float[] getPositions() {
        return positions.clone();
    }

    public float[] getNormals() {
        return normals != null ? normals.clone() : null;
    }

    public float[] getTangents() {
        return tangents != null ? tangents.clone() : null;
    }

    public Map<Integer, float[]> getTextureCoordinates() {
        Map<Integer, float[]> copy = new HashMap<>();

        textureCoordinates.forEach((Integer key, float[] value) ->
                copy.put(key, value.clone())
        );

        return Map.copyOf(copy);
    }

    public float[] getColors() {
        return colors != null ? colors.clone() : null;
    }

    public int[] getIndices() {
        return indices != null ? indices.clone() : null;
    }

    public PrimitiveMode getPrimitiveMode() {
        return primitiveMode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;

        private float[] positions;
        private float[] normals;
        private float[] tangents;

        private final Map<Integer, float[]> textureCoordinates = new HashMap<>();

        private float[] colors;
        private int[] indices;

        private PrimitiveMode primitiveMode = PrimitiveMode.TRIANGLES;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder positions(float[] positions) {
            this.positions = Objects.requireNonNull(positions).clone();
            return this;
        }

        public Builder normals(float[] normals) {
            this.normals = normals != null ? normals.clone() : null;
            return this;
        }

        public Builder tangents(float[] tangents) {
            this.tangents = tangents != null ? tangents.clone() : null;
            return this;
        }

        public Builder textureCoordinates(int texCoordIndex, float[] textureCoordinates) {
            this.textureCoordinates.put(texCoordIndex, Objects.requireNonNull(textureCoordinates).clone());
            return this;
        }

        public Builder colors(float[] colors) {
            this.colors = colors != null ? colors.clone() : null;
            return this;
        }

        public Builder indices(int[] indices) {
            this.indices = indices != null ? indices.clone() : null;
            return this;
        }

        public Builder primitiveMode(PrimitiveMode primitiveMode) {
            this.primitiveMode = Objects.requireNonNull(primitiveMode);
            return this;
        }

        public MeshAsset build() {
            if (positions == null) {
                throw new IllegalStateException("Mesh positions are required");
            }

            return new MeshAsset(this);
        }
    }

    public enum PrimitiveMode {
        POINTS,
        LINES,
        LINE_LOOP,
        LINE_STRIP,
        TRIANGLES,
        TRIANGLE_STRIP,
        TRIANGLE_FAN
    }
}
