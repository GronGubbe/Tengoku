package net.grongubbe.tengoku.client.input.binding;

import net.grongubbe.tengoku.client.input.Key;

import java.util.Objects;

public record KeyBinding(Key key) implements InputBinding {
    public KeyBinding {
        Objects.requireNonNull(key, "key");
    }
}