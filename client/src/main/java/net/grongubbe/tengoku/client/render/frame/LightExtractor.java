package net.grongubbe.tengoku.client.render.frame;

import net.grongubbe.tengoku.client.scene.components.LightComponent;
import net.grongubbe.tengoku.client.scene.components.TransformComponent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;

public final class LightExtractor {
    private final Vector3f position = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();

    public void extract(RenderFrame frame, TransformComponent transform, LightComponent lightComponent) {
        Objects.requireNonNull(frame, "frame");
        Objects.requireNonNull(transform, "transform");
        Objects.requireNonNull(lightComponent, "lightComponent");

        transform.position(position);
        transform.rotation(rotation);

        frame.addLight(new RenderLight(lightComponent.light(), position, rotation));
    }
}
