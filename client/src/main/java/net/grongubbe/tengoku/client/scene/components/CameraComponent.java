package net.grongubbe.tengoku.client.scene.components;

import net.grongubbe.tengoku.client.scene.Component;
import net.grongubbe.tengoku.client.scene.camera.Camera;

import java.util.Objects;

public final class CameraComponent implements Component {
    private final Camera camera;

    public CameraComponent(Camera camera) {
        this.camera = Objects.requireNonNull(camera, "camera");
    }

    public Camera camera() {
        return camera;
    }
}