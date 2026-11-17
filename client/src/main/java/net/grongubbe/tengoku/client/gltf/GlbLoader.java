package net.grongubbe.tengoku.client.gltf;

import net.grongubbe.tengoku.client.asset.assets.ModelAsset;
import net.grongubbe.tengoku.common.io.FileIO;

import java.io.IOException;

public class GlbLoader {
    private final GlbReader reader = new GlbReader();
    private final GltfImporter importer = new GltfImporter();

    public ModelAsset load(String path) throws IOException {
        GlbData data = reader.read(FileIO.readBytes(path));

        return importer.importModel(data);
    }
}
