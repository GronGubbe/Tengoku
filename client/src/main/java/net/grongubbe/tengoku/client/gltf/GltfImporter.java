package net.grongubbe.tengoku.client.gltf;

import com.google.gson.*;
import net.grongubbe.tengoku.client.asset.assets.MeshAsset;
import net.grongubbe.tengoku.client.asset.assets.ModelAsset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class GltfImporter {
    private static final Logger LOGGER = LogManager.getLogger(GltfImporter.class);

    private static final int GLTF_DEFAULT_PRIMITIVE_MODE = 4;

    private final Gson gson = new Gson();

    ModelAsset importModel(GlbData data) {
        GltfAccessorReader accessorReader = new GltfAccessorReader(data.binaryData());

        JsonObject root = JsonParser.parseString(data.json()).getAsJsonObject();

        JsonArray meshes = root.getAsJsonArray("meshes");

        List<List<MeshAsset>> meshAssets = new ArrayList<>();
        List<ModelAsset.ModelPart> modelParts = new ArrayList<>();

        for (JsonElement meshElement : meshes) {
            JsonObject mesh = meshElement.getAsJsonObject();
            JsonArray primitives = mesh.getAsJsonArray("primitives");

            List<MeshAsset> primitiveAssets = new ArrayList<>();

            for (JsonElement primitiveElement : primitives) {
                JsonObject primitive = primitiveElement.getAsJsonObject();
                JsonObject attributes = primitive.get("attributes").getAsJsonObject();

                MeshAsset.Builder meshBuilder = MeshAsset.builder();

                for (Map.Entry<String, JsonElement> attribute : attributes.entrySet()) {
                    String name = attribute.getKey();
                    int accessorIndex = attribute.getValue().getAsInt();

                    if (name.startsWith("TEXCOORD_")) {
                        int texCoordIndex = Integer.parseInt(name.substring("TEXCOORD_".length()));

                        meshBuilder.textureCoordinates(texCoordIndex, readFloats(gson, accessorReader, root, accessorIndex));
                    } else if (name.startsWith("COLOR_")) {
                        LOGGER.warn("Color attributes are not supported");
                    } else if (name.startsWith("JOINTS_")) {
                        LOGGER.warn("Joint attributes are not supported");
                    } else if (name.startsWith("WEIGHTS_")) {
                        LOGGER.warn("Weight attributes are not supported");
                    } else {
                        switch (name) {
                            case "POSITION" -> meshBuilder.positions(readFloats(gson, accessorReader, root, accessorIndex));
                            case "NORMAL" -> meshBuilder.normals(readFloats(gson, accessorReader, root, accessorIndex));
                            case "TANGENT" -> meshBuilder.tangents(readFloats(gson, accessorReader, root, accessorIndex));
                            default -> LOGGER.warn("{} attributes are not supported", name);
                        }
                    }
                }

                if (primitive.has("indices")) {
                    int indicesIndex = primitive.get("indices").getAsInt();
                    meshBuilder.indices(readIndices(gson, accessorReader, root, indicesIndex));
                }

                meshBuilder.name(mesh.has("name") ? mesh.get("name").getAsString() : null);
                meshBuilder.primitiveMode(getPrimitiveMode(primitive.has("mode") ? primitive.get("mode").getAsInt() : GLTF_DEFAULT_PRIMITIVE_MODE));

                primitiveAssets.add(meshBuilder.build());
            }

            meshAssets.add(primitiveAssets);
        }

        JsonArray nodes = root.getAsJsonArray("nodes");

        List<GltfNode> gltfNodes = new ArrayList<>();

        for (JsonElement nodeElement : nodes) {
            JsonObject node = nodeElement.getAsJsonObject();

            String name = node.has("name") ? node.get("name").getAsString() : null;
            Integer mesh = node.has("mesh") ? node.get("mesh").getAsInt() : null;

            JsonArray translationArray = node.has("translation") ? node.getAsJsonArray("translation") : null;
            JsonArray rotationArray = node.has("rotation") ? node.get("rotation").getAsJsonArray() : null;
            JsonArray scaleArray = node.has("scale") ? node.get("scale").getAsJsonArray() : null;

            List<Integer> children = new ArrayList<>();
            Vector3f translation = new Vector3f();
            Quaternionf rotation = new Quaternionf();
            Vector3f scale = new Vector3f(1.0f);

            if (node.has("children")) {
                for (JsonElement child : node.getAsJsonArray("children")) {
                    children.add(child.getAsInt());
                }
            }

            if (translationArray != null) {
                translation.set(
                        translationArray.get(0).getAsFloat(),
                        translationArray.get(1).getAsFloat(),
                        translationArray.get(2).getAsFloat()
                );
            }

            if (rotationArray != null) {
                rotation.set(
                        rotationArray.get(0).getAsFloat(),
                        rotationArray.get(1).getAsFloat(),
                        rotationArray.get(2).getAsFloat(),
                        rotationArray.get(3).getAsFloat()
                );
            }

            if (scaleArray != null) {
                scale.set(
                        scaleArray.get(0).getAsFloat(),
                        scaleArray.get(1).getAsFloat(),
                        scaleArray.get(2).getAsFloat()
                );
            }

            gltfNodes.add(new GltfNode(name, mesh, children, translation, rotation, scale));
        }

        int rootSceneIndex = root.get("scene").getAsInt();

        JsonObject scene = root.getAsJsonArray("scenes").get(rootSceneIndex).getAsJsonObject();
        JsonArray rootNodes = scene.getAsJsonArray("nodes");
        int[] rootNodeIndices = rootNodes.asList().stream().mapToInt(JsonElement::getAsInt).toArray();

        for (int rootNodeIndex : rootNodeIndices) {
            GltfNode current = gltfNodes.get(rootNodeIndex);

            traverseNode(current, gltfNodes, meshAssets, modelParts, new Matrix4f());
        }

        return new ModelAsset(null, modelParts);
    }

    private MeshAsset.PrimitiveMode getPrimitiveMode(int mode) {
        return switch (mode) {
            case 0 -> MeshAsset.PrimitiveMode.POINTS;
            case 1 -> MeshAsset.PrimitiveMode.LINES;
            case 2 -> MeshAsset.PrimitiveMode.LINE_LOOP;
            case 3 -> MeshAsset.PrimitiveMode.LINE_STRIP;
            case 4 -> MeshAsset.PrimitiveMode.TRIANGLES;
            case 5 -> MeshAsset.PrimitiveMode.TRIANGLE_STRIP;
            case 6 -> MeshAsset.PrimitiveMode.TRIANGLE_FAN;
            default -> throw new IllegalArgumentException("Unsupported glTF primitive mode: " + mode);
        };
    }

    private void traverseNode(GltfNode node, List<GltfNode> gltfNodes, List<List<MeshAsset>> meshAssets, List<ModelAsset.ModelPart> modelParts, Matrix4f parentTransform) {
        Integer meshIndex = node.mesh();
        Matrix4f localTransform = new Matrix4f().translationRotateScale(node.translation(), node.rotation(), node.scale());
        Matrix4f worldTransform = new Matrix4f(parentTransform).mul(localTransform);

        if (meshIndex != null) {
            List<MeshAsset> primitives = meshAssets.get(meshIndex);

            for (MeshAsset primitive : primitives) {
                ModelAsset.ModelPart modelpart = new ModelAsset.ModelPart(primitive, null, worldTransform);
                modelParts.add(modelpart);
            }
        }

        for (int childIndex : node.children()) {
            GltfNode child = gltfNodes.get(childIndex);

            traverseNode(child, gltfNodes, meshAssets, modelParts, worldTransform);
        }
    }

    private float[] readFloats(Gson gson, GltfAccessorReader accessorReader, JsonObject root, int accessorIndex) {
        JsonArray accessors = root.getAsJsonArray("accessors");
        JsonArray bufferViews = root.getAsJsonArray("bufferViews");

        JsonObject accessor = accessors.get(accessorIndex).getAsJsonObject();
        int bufferViewIndex = accessor.get("bufferView").getAsInt();

        JsonObject bufferView = bufferViews.get(bufferViewIndex).getAsJsonObject();

        GltfAccessor gltfAccessor = gson.fromJson(accessor, GltfAccessor.class);
        GltfBufferView gltfBufferView = gson.fromJson(bufferView, GltfBufferView.class);

        return accessorReader.readFloats(gltfAccessor, gltfBufferView);
    }

    private int[] readIndices(Gson gson, GltfAccessorReader accessorReader, JsonObject root, int accessorIndex) {
        JsonArray accessors = root.getAsJsonArray("accessors");
        JsonArray bufferViews = root.getAsJsonArray("bufferViews");

        JsonObject accessor = accessors.get(accessorIndex).getAsJsonObject();
        int bufferViewIndex = accessor.get("bufferView").getAsInt();

        JsonObject bufferView = bufferViews.get(bufferViewIndex).getAsJsonObject();

        GltfAccessor gltfAccessor = gson.fromJson(accessor, GltfAccessor.class);
        GltfBufferView gltfBufferView = gson.fromJson(bufferView, GltfBufferView.class);

        return accessorReader.readIndices(gltfAccessor, gltfBufferView);
    }
}