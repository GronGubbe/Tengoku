package net.grongubbe.tengoku.client.scene.light;

public final class SpotLight extends Light {
    private float range = 10.0f;
    private float innerAngle = (float) Math.toRadians(15.0);
    private float outerAngle = (float) Math.toRadians(30.0);

    public float range() {
        return range;
    }

    public float innerAngle() {
        return innerAngle;
    }

    public float outerAngle() {
        return outerAngle;
    }

    public void setRange(float range) {
        if (range <= 0.0f) {
            throw new IllegalArgumentException("Light range must be > 0");
        }

        this.range = range;
    }

    public void setInnerAngle(float innerAngle) {
        validateAngle(innerAngle, "innerAngle");

        if (innerAngle > outerAngle) {
            throw new IllegalArgumentException("Inner angle must be <= outer angle");
        }

        this.innerAngle = innerAngle;
    }

    public void setOuterAngle(float outerAngle) {
        validateAngle(outerAngle, "outerAngle");

        if (outerAngle < innerAngle) {
            throw new IllegalArgumentException("Outer angle must be >= inner angle");
        }

        this.outerAngle = outerAngle;
    }

    private static void validateAngle(float angle, String name) {
        if (angle < 0.0f || angle > Math.PI) {
            throw new IllegalArgumentException(name + " must be between 0 and PI");
        }
    }
}
