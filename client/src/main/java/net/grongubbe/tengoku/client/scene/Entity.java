package net.grongubbe.tengoku.client.scene;

import java.util.Objects;

public final class Entity {
    private final int id;

    Entity(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Entity other)) {
            return false;
        }

        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Entity[" + id + "]";
    }
}