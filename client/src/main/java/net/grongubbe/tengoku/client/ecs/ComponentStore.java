package net.grongubbe.tengoku.client.ecs;

import java.util.HashMap;
import java.util.Map;

class ComponentStore<T extends IComponent> {
    private final Map<Integer, T> components = new HashMap<>();

    public void add(int entity, T component) {
        if (has(entity)) {
            throw new IllegalArgumentException("Entity already has a component of this type");
        }

        components.put(entity, component);
    }

    public T get(int entity) {
        return components.get(entity);
    }

    public boolean has(int entity) {
        return components.containsKey(entity);
    }

    public void remove(int entity) {
        components.remove(entity);
    }
}