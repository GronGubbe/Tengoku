package net.grongubbe.tengoku.client.scene.components;

import net.grongubbe.tengoku.client.scene.Component;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Objects;

public final class TransformComponent implements Component {
    private final Vector3f position = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();
    private final Vector3f scale = new Vector3f(1.0f);

    private final Matrix4f matrix = new Matrix4f();

    private boolean dirty = true;

    public Matrix4f matrix(Matrix4f destination) {
        Objects.requireNonNull(destination, "destination");

        if (dirty) {
            rebuildMatrix();
        }

        return destination.set(matrix);
    }

    public Vector3f position(Vector3f destination) {
        Objects.requireNonNull(destination, "destination");
        return destination.set(position);
    }

    public Quaternionf rotation(Quaternionf destination) {
        Objects.requireNonNull(destination, "destination");
        return destination.set(rotation);
    }

    public Vector3f scale(Vector3f destination) {
        Objects.requireNonNull(destination, "destination");
        return destination.set(scale);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        markDirty();
    }

    public void translate(float x, float y, float z) {
        position.add(x, y, z);
        markDirty();
    }

    public void translate(Vector3fc translation) {
        position.add(translation);
        markDirty();
    }

    public void setRotation(Quaternionf rotation) {
        Objects.requireNonNull(rotation, "rotation");

        this.rotation.set(rotation);
        markDirty();
    }

    public void rotate(Quaternionf rotation) {
        Objects.requireNonNull(rotation, "rotation");

        this.rotation.mul(rotation);
        markDirty();
    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
        markDirty();
    }

    public void setScale(float scale) {
        this.scale.set(scale, scale, scale);
        markDirty();
    }

    private void rebuildMatrix() {
        matrix.identity().translate(position).rotate(rotation).scale(scale);

        dirty = false;
    }

    private void markDirty() {
        dirty = true;
    }
}