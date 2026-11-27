package net.grongubbe.tengoku.client.gltf;

import com.google.gson.*;
import net.grongubbe.tengoku.client.asset.assets.MaterialAsset;
import net.grongubbe.tengoku.client.asset.assets.MeshAsset;
import net.grongubbe.tengoku.client.asset.assets.ModelAsset;
import net.grongubbe.tengoku.client.asset.assets.TextureAsset;
import net.grongubbe.tengoku.client.material.AlphaMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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

        MeshData meshData = loadMeshes(root, accessorReader);

        List<TextureAsset> textures = loadTextures(root, data.binaryData());
        List<MaterialAsset> materials = loadMaterials(root, textures);

        List<GltfNode> nodes = getNodes(root);

        List<ModelAsset.ModelPart> modelParts = new ArrayList<>();

        int rootSceneIndex = root.get("scene").getAsInt();

        JsonObject scene = root.getAsJsonArray("scenes").get(rootSceneIndex).getAsJsonObject();
        JsonArray rootNodes = scene.getAsJsonArray("nodes");
        int[] rootNodeIndices = rootNodes.asList().stream().mapToInt(JsonElement::getAsInt).toArray();

        for (int rootNodeIndex : rootNodeIndices) {
            GltfNode current = nodes.get(rootNodeIndex);

            traverseNode(current, nodes, meshData.meshes(), meshData.materialIndices(), materials, modelParts, new Matrix4f());
        }

        return new ModelAsset(null, modelParts);
    }

    private MeshData loadMeshes(JsonObject root, GltfAccessorReader accessorReader) {
        JsonArray meshes = root.getAsJsonArray("meshes");

        List<List<MeshAsset>> meshAssets = new ArrayList<>();
        List<List<Integer>> materialIndices = new ArrayList<>();

        for (JsonElement meshElement : meshes) {
            JsonObject mesh = meshElement.getAsJsonObject();
            JsonArray primitives = mesh.getAsJsonArray("primitives");

            List<MeshAsset> primitiveAssets = new ArrayList<>();
            List<Integer> primitiveMaterialIndices = new ArrayList<>();

            for (JsonElement primitiveElement : primitives) {
                JsonObject primitive = primitiveElement.getAsJsonObject();
                JsonObject attributes = primitive.get("attributes").getAsJsonObject();
                Integer materialIndex = primitive.has("material") ? primitive.get("material").getAsInt() : null;

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
                primitiveMaterialIndices.add(materialIndex);
            }

            meshAssets.add(primitiveAssets);
            materialIndices.add(primitiveMaterialIndices);
        }

        return new MeshData(meshAssets, materialIndices);
    }

    private List<TextureAsset> loadTextures(JsonObject root, byte[] binaryData) {
        List<TextureAsset> textureAssets = new ArrayList<>();

        JsonArray textures = root.getAsJsonArray("textures");

        if (textures == null) {
            return textureAssets;
        }

        List<DecodedImage> decodedImages = new ArrayList<>();

        JsonArray images = root.getAsJsonArray("images");

        for (JsonElement imageElement : images) {
            JsonObject image = imageElement.getAsJsonObject();

            byte[] imageData = readImageBytes(root, binaryData, image);
            DecodedImage decodedImage = decodeImage(imageData);
            decodedImages.add(decodedImage);
        }

        JsonArray samplers = root.getAsJsonArray("samplers");

        for (JsonElement textureElement : textures) {
            JsonObject texture = textureElement.getAsJsonObject();

            if (!texture.has("source")) {
                throw new UnsupportedOperationException("Texture has no supported image source");
            }

            int sourceIndex = texture.get("source").getAsInt();
            DecodedImage decodedImage = decodedImages.get(sourceIndex);

            TextureAsset.Builder builder = TextureAsset.builder();

            if (texture.has("sampler")) {
                if (samplers == null) {
                    throw new IllegalArgumentException("Texture references a sampler, but no samplers are defined");
                }

                int samplerIndex = texture.get("sampler").getAsInt();
                JsonObject sampler = samplers.get(samplerIndex).getAsJsonObject();

                applySampler(sampler, builder);
            }

            if (texture.has("name")) {
                builder.name(texture.get("name").getAsString());
            }

            textureAssets.add(builder.pixelData(decodedImage.pixelData).width(decodedImage.width).height(decodedImage.height).build());
        }

        return textureAssets;
    }

    private byte[] readImageBytes(JsonObject root, byte[] binaryData, JsonObject image) {
        if (image.has("bufferView")) {
            int bufferViewIndex = image.get("bufferView").getAsInt();

            JsonObject bufferView = root.getAsJsonArray("bufferViews").get(bufferViewIndex).getAsJsonObject();
            GltfBufferView gltfBufferView = gson.fromJson(bufferView, GltfBufferView.class);

            if (gltfBufferView.buffer() != 0) {
                throw new UnsupportedOperationException("Unsupported buffer index: " + gltfBufferView.buffer());
            }

            ByteBuffer buffer = ByteBuffer.wrap(binaryData);

            int start = gltfBufferView.byteOffset();
            int length = gltfBufferView.byteLength();

            if (start < 0 || length < 0 || start > binaryData.length - length) {
                throw new IllegalStateException("Image data exceeds binary buffer bounds");
            }

            buffer.position(start);
            buffer.limit(start + length);

            byte[] imageData = new byte[length];
            buffer.get(imageData);

            return imageData;
        } else if (image.has("uri")) {
            throw new UnsupportedOperationException("External images not supported");
        }

        throw new UnsupportedOperationException("Image " + image.get("name") + " has neither a bufferView nor a URI");
    }

    private DecodedImage decodeImage(byte[] imageData) {
        ByteArrayInputStream input = new ByteArrayInputStream(imageData);
        BufferedImage image;

        try {
            image = ImageIO.read(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (image == null) {
            throw new IllegalArgumentException("Unsupported or invalid image format");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        ByteBuffer pixelBuffer = ByteBuffer.allocate(Math.multiplyExact(Math.multiplyExact(width, height), 4));

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int pixel = image.getRGB(column, row);

                int blue = pixel & 0xFF;
                int green = (pixel >>> 8) & 0xFF;
                int red = (pixel >>> 16) & 0xFF;
                int alpha = (pixel >>> 24) & 0xFF;

                pixelBuffer.put((byte) red);
                pixelBuffer.put((byte) green);
                pixelBuffer.put((byte) blue);
                pixelBuffer.put((byte) alpha);
            }
        }

        byte[] pixelData = pixelBuffer.array();
        return new DecodedImage(width, height, pixelData);
    }

    private void applySampler(JsonObject sampler, TextureAsset.Builder builder) {
        if (sampler.has("magFilter")) {
            switch (sampler.get("magFilter").getAsInt()) {
                case 9728 -> builder.magFilter(TextureAsset.MagFilter.NEAREST);
                case 9729 -> builder.magFilter(TextureAsset.MagFilter.LINEAR);
                default -> throw new IllegalArgumentException("Illegal magFilter: " + sampler.get("magFilter").getAsInt());
            }
        }

        if (sampler.has("minFilter")) {
            switch (sampler.get("minFilter").getAsInt()) {
                case 9728 -> builder.minFilter(TextureAsset.MinFilter.NEAREST);
                case 9729 -> builder.minFilter(TextureAsset.MinFilter.LINEAR);
                case 9984 -> builder.minFilter(TextureAsset.MinFilter.NEAREST_MIPMAP_NEAREST);
                case 9985 -> builder.minFilter(TextureAsset.MinFilter.LINEAR_MIPMAP_NEAREST);
                case 9986 -> builder.minFilter(TextureAsset.MinFilter.NEAREST_MIPMAP_LINEAR);
                case 9987 -> builder.minFilter(TextureAsset.MinFilter.LINEAR_MIPMAP_LINEAR);
                default -> throw new IllegalArgumentException("Illegal minFilter: " + sampler.get("minFilter").getAsInt());
            }
        }

        if (sampler.has("wrapS")) {
            switch (sampler.get("wrapS").getAsInt()) {
                case 33071 -> builder.horizontalWrap(TextureAsset.WrapMode.CLAMP_TO_EDGE);
                case 33648 -> builder.horizontalWrap(TextureAsset.WrapMode.MIRRORED_REPEAT);
                case 10497 -> builder.horizontalWrap(TextureAsset.WrapMode.REPEAT);
                default -> throw new IllegalArgumentException("Illegal wrapS: " + sampler.get("wrapS").getAsInt());
            }
        }

        if (sampler.has("wrapT")) {
            switch (sampler.get("wrapT").getAsInt()) {
                case 33071 -> builder.verticalWrap(TextureAsset.WrapMode.CLAMP_TO_EDGE);
                case 33648 -> builder.verticalWrap(TextureAsset.WrapMode.MIRRORED_REPEAT);
                case 10497 -> builder.verticalWrap(TextureAsset.WrapMode.REPEAT);
                default -> throw new IllegalArgumentException("Illegal wrapT: " + sampler.get("wrapT").getAsInt());
            }
        }
    }

    private List<MaterialAsset> loadMaterials(JsonObject root, List<TextureAsset> textures) {
        List<MaterialAsset> materialAssets = new ArrayList<>();

        JsonArray materials = root.getAsJsonArray("materials");
        if (materials == null) {
            return materialAssets;
        }

        for (JsonElement materialElement : materials) {
            JsonObject material = materialElement.getAsJsonObject();
            MaterialAsset.Builder materialBuilder = MaterialAsset.builder();

            if (material.has("name")) {
                materialBuilder.name(material.get("name").getAsString());
            }

            if (material.has("pbrMetallicRoughness")) {
                JsonObject pbrMetallicRoughness = material.getAsJsonObject("pbrMetallicRoughness");

                if (pbrMetallicRoughness.has("baseColorFactor")) {
                    JsonArray colorComponents = pbrMetallicRoughness.get("baseColorFactor").getAsJsonArray();
                    Vector4f rgba = new Vector4f(
                            colorComponents.get(0).getAsFloat(),
                            colorComponents.get(1).getAsFloat(),
                            colorComponents.get(2).getAsFloat(),
                            colorComponents.get(3).getAsFloat()
                    );

                    materialBuilder.baseColorFactor(rgba);
                }

                if (pbrMetallicRoughness.has("baseColorTexture")) {
                    JsonObject baseColorTexture = pbrMetallicRoughness.get("baseColorTexture").getAsJsonObject();

                    int textureIndex = baseColorTexture.get("index").getAsInt();
                    materialBuilder.baseColorTexture(textures.get(textureIndex));

                    if (baseColorTexture.has("texCoord")) {
                        materialBuilder.baseColorTexCoord(baseColorTexture.get("texCoord").getAsInt());
                    }
                }

                if (pbrMetallicRoughness.has("metallicFactor")) {
                    materialBuilder.metallicFactor(pbrMetallicRoughness.get("metallicFactor").getAsFloat());
                }

                if (pbrMetallicRoughness.has("roughnessFactor")) {
                    materialBuilder.roughnessFactor(pbrMetallicRoughness.get("roughnessFactor").getAsFloat());
                }

                if (pbrMetallicRoughness.has("metallicRoughnessTexture")) {
                    JsonObject metallicRoughnessTexture = pbrMetallicRoughness.get("metallicRoughnessTexture").getAsJsonObject();

                    int index = metallicRoughnessTexture.get("index").getAsInt();
                    materialBuilder.metallicRoughnessTexture(textures.get(index));

                    if (metallicRoughnessTexture.has("texCoord")) {
                        materialBuilder.metallicRoughnessTexCoord(metallicRoughnessTexture.get("texCoord").getAsInt());
                    }
                }
            }

            if (material.has("normalTexture")) {
                JsonObject normalTexture = material.get("normalTexture").getAsJsonObject();

                int index = normalTexture.get("index").getAsInt();
                materialBuilder.normalTexture(textures.get(index));

                if (normalTexture.has("texCoord")) {
                    materialBuilder.normalTexCoord(normalTexture.get("texCoord").getAsInt());
                }

                if (normalTexture.has("scale")) {
                    materialBuilder.normalScale(normalTexture.get("scale").getAsFloat());
                }
            }

            if (material.has("occlusionTexture")) {
                JsonObject occlusionTexture = material.get("occlusionTexture").getAsJsonObject();

                int index = occlusionTexture.get("index").getAsInt();
                materialBuilder.occlusionTexture(textures.get(index));

                if (occlusionTexture.has("texCoord")) {
                    materialBuilder.occlusionTexCoord(occlusionTexture.get("texCoord").getAsInt());
                }

                if (occlusionTexture.has("strength")) {
                    materialBuilder.occlusionStrength(occlusionTexture.get("strength").getAsFloat());
                }
            }

            if (material.has("emissiveFactor")) {
                JsonArray emissiveComponents = material.get("emissiveFactor").getAsJsonArray();
                Vector3f emissiveFactor = new Vector3f(
                        emissiveComponents.get(0).getAsFloat(),
                        emissiveComponents.get(1).getAsFloat(),
                        emissiveComponents.get(2).getAsFloat()
                );

                materialBuilder.emissiveFactor(emissiveFactor);
            }

            if (material.has("emissiveTexture")) {
                JsonObject emissiveTexture = material.get("emissiveTexture").getAsJsonObject();

                int index = emissiveTexture.get("index").getAsInt();
                materialBuilder.emissiveTexture(textures.get(index));

                if (emissiveTexture.has("texCoord")) {
                    materialBuilder.emissiveTexCoord(emissiveTexture.get("texCoord").getAsInt());
                }
            }

            if (material.has("alphaMode")) {
                String alphaMode = material.get("alphaMode").getAsString();
                materialBuilder.alphaMode(AlphaMode.valueOf(alphaMode));
            }

            if (material.has("alphaCutoff")) {
                float alphaCutoff = material.get("alphaCutoff").getAsFloat();
                materialBuilder.alphaCutoff(alphaCutoff);
            }

            if (material.has("doubleSided")) {
                boolean doubleSided = material.get("doubleSided").getAsBoolean();
                materialBuilder.doubleSided(doubleSided);
            }

            materialAssets.add(materialBuilder.build());
        }

        return materialAssets;
    }

    private List<GltfNode> getNodes(JsonObject root) {
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

        return gltfNodes;
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

    private void traverseNode(GltfNode node, List<GltfNode> gltfNodes, List<List<MeshAsset>> meshAssets, List<List<Integer>> materialIndices, List<MaterialAsset> materialAssets, List<ModelAsset.ModelPart> modelParts, Matrix4f parentTransform) {
        Integer meshIndex = node.mesh();
        Matrix4f localTransform = new Matrix4f().translationRotateScale(node.translation(), node.rotation(), node.scale());
        Matrix4f worldTransform = new Matrix4f(parentTransform).mul(localTransform);

        if (meshIndex != null) {
            List<MeshAsset> primitives = meshAssets.get(meshIndex);

            for (int i = 0; i < primitives.size(); i++) {
                MeshAsset primitive = primitives.get(i);

                Integer materialIndex = materialIndices.get(meshIndex).get(i);
                MaterialAsset material = materialIndex != null ? materialAssets.get(materialIndex) : MaterialAsset.builder().build();

                ModelAsset.ModelPart modelPart = new ModelAsset.ModelPart(primitive, material, worldTransform);

                modelParts.add(modelPart);
            }
        }

        for (int childIndex : node.children()) {
            GltfNode child = gltfNodes.get(childIndex);

            traverseNode(child, gltfNodes, meshAssets, materialIndices, materialAssets, modelParts, worldTransform);
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

    private record MeshData(List<List<MeshAsset>> meshes, List<List<Integer>> materialIndices) {
    }

    private record DecodedImage(int width, int height, byte[] pixelData) {
    }
}