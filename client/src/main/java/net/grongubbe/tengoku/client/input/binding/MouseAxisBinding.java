package net.grongubbe.tengoku.client.input.binding;

import java.util.Objects;

public record MouseAxisBinding(MouseAxis axis, float scale) implements AxisBinding {
    public MouseAxisBinding {
        Objects.requireNonNull(axis, "axis");

        if (scale == 0.0f) {
            throw new IllegalArgumentException("Mouse axis scale must not be zero");
        }
    }
}