package net.grongubbe.tengoku.client.asset.mesh;

import net.grongubbe.tengoku.client.asset.AssetLoader;
import net.grongubbe.tengoku.client.asset.AssetLoadingContext;
import net.grongubbe.tengoku.client.asset.mesh.importer.MeshImporter;
import net.grongubbe.tengoku.client.asset.serialization.AssetDeserializer;
import net.grongubbe.tengoku.client.asset.serialization.mesh.MeshDefinition;
import net.grongubbe.tengoku.common.util.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

public final class MeshLoader implements AssetLoader<MeshKey, Mesh> {
    private final AssetDeserializer<MeshDefinition> deserializer;

    private final MeshImporter importer;

    public MeshLoader(AssetDeserializer<MeshDefinition> deserializer, MeshImporter importer) {
        this.deserializer = deserializer;
        this.importer = importer;
    }

    @Override
    public Mesh load(MeshKey key, AssetLoadingContext context) throws IOException {
        try (InputStream stream = ResourceLoader.open(key.path())) {
            MeshDefinition definition = deserializer.deserialize(stream);

            try (InputStream model = ResourceLoader.open(definition.model())) {
                Mesh imported = importer.importMesh(model);

                return new Mesh(imported.data(), imported.subMeshes());
            }
        }
    }
}