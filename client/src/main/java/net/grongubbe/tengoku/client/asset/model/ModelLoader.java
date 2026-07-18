package net.grongubbe.tengoku.client.asset.model;

import net.grongubbe.tengoku.client.asset.AssetLoader;
import net.grongubbe.tengoku.client.asset.AssetLoadingContext;
import net.grongubbe.tengoku.client.asset.material.Material;
import net.grongubbe.tengoku.client.asset.material.MaterialKey;
import net.grongubbe.tengoku.client.asset.mesh.Mesh;
import net.grongubbe.tengoku.client.asset.mesh.MeshKey;
import net.grongubbe.tengoku.client.asset.serialization.model.ModelDefinition;
import net.grongubbe.tengoku.client.asset.serialization.model.ModelDeserializer;
import net.grongubbe.tengoku.client.asset.serialization.model.ModelPartDefinition;
import net.grongubbe.tengoku.common.util.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class ModelLoader implements AssetLoader<ModelKey, Model> {
    private final ModelDeserializer deserializer;

    public ModelLoader(ModelDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    @Override
    public Model load(ModelKey key, AssetLoadingContext context) throws IOException {
        try (InputStream stream = ResourceLoader.open(key.path())) {
            ModelDefinition definition = deserializer.deserialize(stream);

            List<ModelPart> parts = new ArrayList<>();

            for (ModelPartDefinition part : definition.parts()) {
                Mesh mesh = context.get(new MeshKey(part.mesh()));

                List<Material> materials = new ArrayList<>();

                for (var material : part.materials()) {
                    materials.add(context.get(new MaterialKey(material)));
                }

                parts.add(new ModelPart(mesh, materials));
            }

            return new Model(parts);
        }
    }
}