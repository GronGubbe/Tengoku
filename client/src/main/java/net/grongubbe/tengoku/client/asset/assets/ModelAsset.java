package net.grongubbe.tengoku.client.asset.assets;

import net.grongubbe.tengoku.client.asset.IAsset;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Objects;

public class ModelAsset implements IAsset {
    private final String name;

    private final List<ModelPart> modelParts;

    public ModelAsset(String name, List<ModelPart> modelParts) {
        this.name = name;
        this.modelParts = List.copyOf(modelParts);
    }

    public String getName() {
        return name;
    }

    public List<ModelPart> getModelParts() {
        return modelParts;
    }

    public record ModelPart(MeshAsset mesh, MaterialAsset material, Matrix4f transform) {
            public ModelPart(MeshAsset mesh, MaterialAsset material, Matrix4f transform) {
                this.mesh = Objects.requireNonNull(mesh, "mesh");
                this.material = Objects.requireNonNull(material, "material");
                this.transform = new Matrix4f(Objects.requireNonNull(transform, "transform"));
            }

            @Override
            public Matrix4f transform() {
                return new Matrix4f(transform);
            }
        }
}
