package net.grongubbe.tengoku.client.input.binding;

import net.grongubbe.tengoku.client.input.Key;

import java.util.Objects;

public record KeyAxisBinding(Key key, float value) implements AxisBinding {
    public KeyAxisBinding {
        Objects.requireNonNull(key, "key");

        if (value == 0.0f) {
            throw new IllegalArgumentException("Axis value must not be zero");
        }
    }
}