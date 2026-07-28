package net.grongubbe.tengoku.client.asset.mesh.importer;

import net.grongubbe.tengoku.client.asset.mesh.*;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ObjMeshImporter implements MeshImporter {

    @Override
    public ImportedMesh importMesh(InputStream input) throws IOException {
        List<Position> positions = new ArrayList<>();
        List<TextureCoordinate> textureCoordinates = new ArrayList<>();
        List<Normal> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] tokens = line.split("\\s+");

                if (tokens.length == 0) {
                    continue;
                }

                try {
                    switch (tokens[0]) {
                        case "v" -> positions.add(parsePosition(tokens));
                        case "vt" -> textureCoordinates.add(parseTextureCoordinate(tokens));
                        case "vn" -> normals.add(parseNormal(tokens));
                        case "f" -> faces.addAll(parseFace(tokens));
                    }
                } catch (IOException exception) {
                    throw new IOException("Failed parsing OBJ line " + lineNumber + ":\n" + line, exception);
                }
            }
        }

        if (positions.isEmpty()) {
            throw new IOException("OBJ contains no vertex positions.");
        }

        if (faces.isEmpty()) {
            throw new IOException("OBJ contains no faces.");
        }

        return buildMesh(positions, textureCoordinates, normals, faces);
    }

    private Position parsePosition(String[] tokens) throws IOException {
        requireTokenCount(tokens, 4, "position");

        return new Position(
                parseFloat(tokens[1], "position"),
                parseFloat(tokens[2], "position"),
                parseFloat(tokens[3], "position")
        );
    }

    private TextureCoordinate parseTextureCoordinate(String[] tokens) throws IOException {
        requireTokenCount(tokens, 3, "texture coordinate");

        return new TextureCoordinate(
                parseFloat(tokens[1], "texture coordinate"),
                parseFloat(tokens[2], "texture coordinate")
        );
    }

    private Normal parseNormal(String[] tokens) throws IOException {
        requireTokenCount(tokens, 4, "normal");

        return new Normal(
                parseFloat(tokens[1], "normal"),
                parseFloat(tokens[2], "normal"),
                parseFloat(tokens[3], "normal")
        );
    }

    private List<Face> parseFace(String[] tokens) throws IOException {
        if (tokens.length < 4) {
            throw new IOException("OBJ face must contain at least three vertices.");
        }

        List<FaceVertex> vertices = new ArrayList<>(tokens.length - 1);

        for (int i = 1; i < tokens.length; i++) {
            vertices.add(parseFaceVertex(tokens[i]));
        }

        if (vertices.size() == 3) {
            return List.of(new Face(vertices));
        }

        if (vertices.size() == 4) {
            return List.of(
                    new Face(List.of(
                            vertices.get(0),
                            vertices.get(1),
                            vertices.get(2)
                    )),
                    new Face(List.of(
                            vertices.get(0),
                            vertices.get(2),
                            vertices.get(3)
                    ))
            );
        }

        throw new IOException("Unsupported polygon with " + vertices.size() + " vertices.");
    }

    private FaceVertex parseFaceVertex(String token) throws IOException {
        String[] indices = token.split("/", -1);

        return new FaceVertex(
                parseIndex(indices, 0),
                parseIndex(indices, 1),
                parseIndex(indices, 2)
        );
    }

    private int parseIndex(String[] indices, int index) throws IOException {
        if (indices.length <= index || indices[index].isEmpty()) {
            return -1;
        }

        try {
            int value = Integer.parseInt(indices[index]);

            if (value <= 0) {
                throw new IOException("OBJ negative indices are not supported: " + value);
            }

            return value - 1;

        } catch (NumberFormatException exception) {
            throw new IOException("Invalid OBJ index: " + indices[index], exception);
        }
    }

    private ImportedMesh buildMesh(List<Position> positions, List<TextureCoordinate> textureCoordinates, List<Normal> normals, List<Face> faces) {
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        Map<VertexKey, Integer> vertexMap = new HashMap<>();

        for (Face face : faces) {
            for (FaceVertex vertex : face.vertices()) {

                VertexKey key = new VertexKey(vertex.position(), vertex.textureCoordinate(), vertex.normal());

                int index = vertexMap.computeIfAbsent(key, ignored -> {

                    Position position = getPosition(positions, key.position());
                    Normal normal = getNormal(normals, key.normal());
                    TextureCoordinate uv = getTextureCoordinate(textureCoordinates, key.textureCoordinate());

                    vertices.add(position.x());
                    vertices.add(position.y());
                    vertices.add(position.z());

                    vertices.add(normal.x());
                    vertices.add(normal.y());
                    vertices.add(normal.z());

                    vertices.add(uv.u());
                    vertices.add(uv.v());

                    return vertexMap.size();
                });

                indices.add(index);
            }
        }

        ByteBuffer vertexBuffer = MemoryUtil.memAlloc(vertices.size() * Float.BYTES);

        for (float vertex : vertices) {
            vertexBuffer.putFloat(vertex);
        }

        vertexBuffer.flip();

        IntBuffer indexBuffer = MemoryUtil.memAllocInt(indices.size());

        for (int index : indices) {
            indexBuffer.put(index);
        }

        indexBuffer.flip();

        VertexLayout layout = new VertexLayout(List.of(
                new VertexAttribute("position", VertexAttributeType.VECTOR3),
                new VertexAttribute("normal", VertexAttributeType.VECTOR3),
                new VertexAttribute("uv", VertexAttributeType.VECTOR2)
        ));

        MeshData data = new MeshData(layout, new VertexBuffer(vertexBuffer), new IndexBuffer(indexBuffer));

        return new ImportedMesh(data, List.of(new MeshSection(0, indices.size(), 0)));
    }

    private Position getPosition(List<Position> positions, int index) {
        if (index < 0 || index >= positions.size()) {
            throw new IllegalStateException("OBJ position index out of bounds: " + index);
        }

        return positions.get(index);
    }

    private Normal getNormal(List<Normal> normals, int index) {
        if (index < 0 || index >= normals.size()) {
            return new Normal(0, 0, 0);
        }

        return normals.get(index);
    }

    private TextureCoordinate getTextureCoordinate(List<TextureCoordinate> coordinates, int index) {
        if (index < 0 || index >= coordinates.size()) {
            return new TextureCoordinate(0, 0);
        }

        return coordinates.get(index);
    }

    private void requireTokenCount(String[] tokens, int expected, String type) throws IOException {
        if (tokens.length != expected) {
            throw new IOException("OBJ " + type + " requires " + (expected - 1) + " values, got " + (tokens.length - 1));
        }
    }

    private float parseFloat(String value, String type) throws IOException {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid float \"" + value + "\" in OBJ " + type, exception);
        }
    }

    private record Position(float x, float y, float z) {
    }

    private record TextureCoordinate(float u, float v) {
    }

    private record Normal(float x, float y, float z) {
    }

    private record Face(List<FaceVertex> vertices) {
    }

    private record FaceVertex(int position, int textureCoordinate, int normal) {
    }

    private record VertexKey(int position, int textureCoordinate, int normal) {
    }
}