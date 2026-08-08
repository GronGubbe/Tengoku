package net.grongubbe.tengoku.client.util;

import net.grongubbe.tengoku.client.scene.components.TransformComponent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class TransformUtils {
    public static void rotateAround(TransformComponent transform, Vector3f point, float angle) {
        Vector3f position = new Vector3f();
        transform.position(position);

        position.sub(point);

        float cosAngle = (float) Math.cos(angle);
        float sinAngle = (float) Math.sin(angle);

        float x = position.x * cosAngle - position.z * sinAngle;
        float z = position.x * sinAngle + position.z * cosAngle;

        position.set(x, position.y, z);
        position.add(point);

        transform.setPosition(position.x, position.y, position.z);
    }

    public static void lookAt(TransformComponent transform, Vector3f target) {
        Vector3f position = new Vector3f();
        transform.position(position);

        Vector3f direction = new Vector3f(target).sub(position).normalize();

        float yaw = (float) Math.atan2(-direction.x, -direction.z);
        float pitch = (float) Math.asin(direction.y);

        Quaternionf rotation = new Quaternionf().rotateY(yaw).rotateX(pitch);

        transform.setRotation(rotation);
    }
}
