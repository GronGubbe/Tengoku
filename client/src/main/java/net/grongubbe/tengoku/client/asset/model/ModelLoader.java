package net.grongubbe.tengoku.client.asset.model;

import net.grongubbe.tengoku.client.asset.AssetLoader;
import net.grongubbe.tengoku.client.asset.AssetLoadingContext;
import net.grongubbe.tengoku.client.asset.bounds.BoundingCalculator;
import net.grongubbe.tengoku.client.asset.bounds.BoundingVolume;
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
import java.nio.file.Path;
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

            for (int partIndex = 0; partIndex < definition.parts().size(); partIndex++) {
                ModelPartDefinition part = definition.parts().get(partIndex);

                Mesh mesh;

                try {
                    mesh = context.get(new MeshKey(part.mesh()));
                } catch (RuntimeException e) {
                    throw new IllegalStateException("""
                            Failed to load model mesh.

                            Model:
                            %s

                            Part:
                            %d

                            Mesh:
                            %s

                            Reason:
                            %s
                            """.formatted(key.path(), partIndex, part.mesh(), e.getMessage()), e
                    );
                }

                List<Material> materials = new ArrayList<>();

                for (Path materialPath : part.materials()) {
                    try {
                        materials.add(context.get(new MaterialKey(materialPath)));
                    } catch (RuntimeException e) {
                        throw new IllegalStateException("""
                                Failed to load model material.

                                Model:
                                %s

                                Part:
                                %d

                                Material:
                                %s

                                Reason:
                                %s
                                """.formatted(key.path(), partIndex, materialPath, e.getMessage()), e
                        );
                    }
                }

                parts.add(new ModelPart(mesh, materials));
            }

            BoundingVolume bounds = BoundingCalculator.calculate(parts);

            return new Model(key, parts, bounds);
        }
    }
}