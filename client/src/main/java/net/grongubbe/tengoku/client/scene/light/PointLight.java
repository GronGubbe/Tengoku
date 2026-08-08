package net.grongubbe.tengoku.client.scene.light;

public final class PointLight extends Light {
    private float range = 10.0f;

    public float range() {
        return range;
    }

    public void setRange(float range) {
        if (range <= 0.0f) {
            throw new IllegalArgumentException("Light range must be > 0");
        }

        this.range = range;
    }
}
