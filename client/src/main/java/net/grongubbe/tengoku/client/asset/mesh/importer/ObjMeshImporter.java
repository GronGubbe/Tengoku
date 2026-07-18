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
    public Mesh importMesh(InputStream input) throws IOException {
        List<Position> positions = new ArrayList<>();
        List<TextureCoordinate> textureCoordinates = new ArrayList<>();
        List<Normal> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] tokens = line.split("\\s+");

                switch (tokens[0]) {
                    case "v" -> positions.add(parsePosition(tokens));
                    case "vt" -> textureCoordinates.add(parseTextureCoordinate(tokens));
                    case "vn" -> normals.add(parseNormal(tokens));
                    case "f" -> faces.addAll(parseFace(tokens));
                }
            }
        }

        return buildMesh(positions, textureCoordinates, normals, faces);
    }

    private Position parsePosition(String[] tokens) throws IOException {
        if (tokens.length != 4) {
            throw new IOException("OBJ position \"" + String.join(" ", tokens) + "\" must contain three coordinates.");
        }

        return new Position(
                Float.parseFloat(tokens[1]),
                Float.parseFloat(tokens[2]),
                Float.parseFloat(tokens[3])
        );
    }

    private TextureCoordinate parseTextureCoordinate(String[] tokens) throws IOException {
        if (tokens.length != 3) {
            throw new IOException("OBJ texture coordinate \"" + String.join(" ", tokens) + "\" must contain two coordinates.");
        }

        return new TextureCoordinate(
                Float.parseFloat(tokens[1]),
                Float.parseFloat(tokens[2])
        );
    }

    private Normal parseNormal(String[] tokens) throws IOException {
        if (tokens.length != 4) {
            throw new IOException("OBJ normal \"" + String.join(" ", tokens) + "\" must contain three coordinates.");
        }

        return new Normal(
                Float.parseFloat(tokens[1]),
                Float.parseFloat(tokens[2]),
                Float.parseFloat(tokens[3])
        );
    }

    private List<Face> parseFace(String[] tokens) throws IOException {
        List<FaceVertex> vertices = new ArrayList<>(tokens.length - 1);

        for (int i = 1; i < tokens.length; i++) {
            vertices.add(parseFaceVertex(tokens[i]));
        }

        if (vertices.size() == 3) {
            return List.of(new Face(vertices));
        }

        if (vertices.size() == 4) {
            return List.of(
                    new Face(List.of(vertices.get(0), vertices.get(1), vertices.get(2))),
                    new Face(List.of(vertices.get(0), vertices.get(2), vertices.get(3)))
            );
        }

        throw new IOException("Unsupported face with " + vertices.size() + " vertices.");
    }

    private FaceVertex parseFaceVertex(String token) throws IOException {
        String[] indices = token.split("/", -1);

        int position = parseIndex(indices, 0);
        int textureCoordinate = parseIndex(indices, 1);
        int normal = parseIndex(indices, 2);

        return new FaceVertex(position, textureCoordinate, normal);
    }

    private int parseIndex(String[] indices, int index) throws IOException {
        if (indices.length <= index || indices[index].isEmpty()) {
            return -1;
        }

        try {
            return Integer.parseInt(indices[index]) - 1;
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid OBJ index: " + indices[index], exception);
        }
    }

    private Mesh buildMesh(List<Position> positions, List<TextureCoordinate> textureCoordinates, List<Normal> normals, List<Face> faces) {
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        Map<VertexKey, Integer> vertexMap = new HashMap<>();

        for (Face face : faces) {
            for (FaceVertex vertex : face.vertices()) {
                VertexKey key = new VertexKey(
                        vertex.position(),
                        vertex.textureCoordinate(),
                        vertex.normal()
                );

                int index = vertexMap.computeIfAbsent(key, ignored -> {
                    Position position = positions.get(key.position());

                    Normal normal = key.normal() >= 0 && key.normal() < normals.size()
                            ? normals.get(key.normal())
                            : new Normal(0, 0, 0);

                    TextureCoordinate uv = key.textureCoordinate() >= 0
                            && key.textureCoordinate() < textureCoordinates.size()
                            ? textureCoordinates.get(key.textureCoordinate())
                            : new TextureCoordinate(0, 0);

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

        // TODO: OBJ Importer should eventually handle material slots through .mtl files
        return new Mesh(data, List.of(
                new MeshSection(0, indices.size(), 0),
                new MeshSection(3, indices.size(), 0)
        ));
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