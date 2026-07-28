package net.grongubbe.tengoku.client.asset.mesh;

import net.grongubbe.tengoku.client.asset.AssetLoader;
import net.grongubbe.tengoku.client.asset.AssetLoadingContext;
import net.grongubbe.tengoku.client.asset.mesh.importer.ImportedMesh;
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
        MeshDefinition definition;

        try (InputStream stream = ResourceLoader.open(key.path())) {
            definition = deserializer.deserialize(stream);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("""
                    Failed to deserialize mesh definition.

                    Mesh:
                    %s

                    Reason:
                    %s
                    """.formatted(key.path(), exception.getMessage()), exception
            );
        }

        ImportedMesh imported;

        try (InputStream model = ResourceLoader.open(definition.model())) {
            imported = importer.importMesh(model);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("""
                    Failed to import mesh.

                    Mesh:
                    %s

                    Source model:
                    %s

                    Reason:
                    %s
                    """.formatted(key.path(), definition.model(), exception.getMessage()), exception
            );
        }

        return new Mesh(key, imported.data(), imported.sections());
    }
}