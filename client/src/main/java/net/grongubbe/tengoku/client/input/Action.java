package net.grongubbe.tengoku.client.input;

import java.util.Objects;

public record Action(String name) {
    public Action {
        Objects.requireNonNull(name, "name");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Action name must not be blank");
        }
    }
}