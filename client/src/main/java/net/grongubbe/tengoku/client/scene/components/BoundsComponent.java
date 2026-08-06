package net.grongubbe.tengoku.client.scene.components;

import net.grongubbe.tengoku.client.asset.bounds.BoundingVolume;
import net.grongubbe.tengoku.client.scene.Component;
import org.joml.Vector3f;

import java.util.Objects;

public final class BoundsComponent implements Component {
    private final BoundingVolume localVolume;

    private final Vector3f worldPositionAlloc = new Vector3f();

    public BoundsComponent(BoundingVolume localVolume) {
        this.localVolume = Objects.requireNonNull(localVolume, "localVolume");
    }

    public BoundingVolume localVolume() {
        return localVolume;
    }

    public BoundingVolume worldVolume(TransformComponent transform) {
        Objects.requireNonNull(transform, "transform");

        return localVolume.translated(transform.position(worldPositionAlloc));
    }
}