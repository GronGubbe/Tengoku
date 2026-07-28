package net.grongubbe.tengoku.client.asset.mesh.importer;

import java.io.IOException;
import java.io.InputStream;

public interface MeshImporter {
    ImportedMesh importMesh(InputStream input) throws IOException;
}