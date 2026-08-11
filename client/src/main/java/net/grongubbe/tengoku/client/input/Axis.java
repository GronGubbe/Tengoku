package net.grongubbe.tengoku.client.input;

import java.util.Objects;

public record Axis(String name) {
    public Axis {
        Objects.requireNonNull(name, "name");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Axis name must not be blank");
        }
    }
}