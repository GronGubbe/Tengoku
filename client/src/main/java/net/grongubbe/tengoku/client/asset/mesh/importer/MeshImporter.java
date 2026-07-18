package net.grongubbe.tengoku.client.asset.mesh.importer;

import net.grongubbe.tengoku.client.asset.mesh.Mesh;

import java.io.IOException;
import java.io.InputStream;

public interface MeshImporter {
    Mesh importMesh(InputStream input) throws IOException;
}