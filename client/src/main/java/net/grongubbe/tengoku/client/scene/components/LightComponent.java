package net.grongubbe.tengoku.client.scene.components;

import net.grongubbe.tengoku.client.scene.Component;
import net.grongubbe.tengoku.client.scene.light.Light;

import java.util.Objects;

public final class LightComponent implements Component {
    private final Light light;

    public LightComponent(Light light) {
        this.light = Objects.requireNonNull(light, "light");
    }

    public Light light() {
        return light;
    }
}