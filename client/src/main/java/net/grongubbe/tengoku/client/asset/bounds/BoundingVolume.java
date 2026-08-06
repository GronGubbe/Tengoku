package net.grongubbe.tengoku.client.asset.bounds;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface BoundingVolume {
    boolean intersects(Vector3f normal, float distance);
    BoundingVolume translated(Vector3fc translation);
}