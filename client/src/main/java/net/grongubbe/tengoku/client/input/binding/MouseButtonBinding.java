package net.grongubbe.tengoku.client.input.binding;

import net.grongubbe.tengoku.client.input.MouseButton;

import java.util.Objects;

public record MouseButtonBinding(MouseButton button) implements InputBinding {
    public MouseButtonBinding {
        Objects.requireNonNull(button, "button");
    }
}