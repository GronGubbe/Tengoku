package net.grongubbe.tengoku.client.gltf;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

record GltfNode(String name, Integer mesh, List<Integer> children, Vector3f translation, Quaternionf rotation, Vector3f scale) {
}
