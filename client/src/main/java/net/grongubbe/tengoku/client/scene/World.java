package net.grongubbe.tengoku.client.scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class World {
    private final Map<Entity, Map<Class<? extends Component>, Component>> components = new HashMap<>();

    private int nextEntityId;

    public Entity createEntity() {
        Entity entity = new Entity(nextEntityId++);

        components.put(entity, new HashMap<>());

        return entity;
    }

    public <T extends Component> void add(Entity entity, T component) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(component, "component");

        Map<Class<? extends Component>, Component> entityComponents = requireEntity(entity);

        Class<? extends Component> type = component.getClass();

        if (entityComponents.containsKey(type)) {
            throw new IllegalStateException("Entity already has component: " + type.getSimpleName());
        }

        entityComponents.put(type, component);
    }

    public <T extends Component> void set(Entity entity, T component) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(component, "component");

        requireEntity(entity).put(component.getClass(), component);
    }

    public <T extends Component> T remove(Entity entity, Class<T> type) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(type, "type");

        Component removed = requireEntity(entity).remove(type);

        if (removed == null) {
            return null;
        }

        return type.cast(removed);
    }

    public <T extends Component> T get(Entity entity, Class<T> type) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(type, "type");

        Component component = requireEntity(entity).get(type);

        if (component == null) {
            return null;
        }

        return type.cast(component);
    }

    public <T extends Component> boolean has(Entity entity, Class<T> type) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(type, "type");

        return requireEntity(entity).containsKey(type);
    }

    public List<Entity> entities() {
        return List.copyOf(components.keySet());
    }

    @SafeVarargs
    public final ComponentQuery query(Class<? extends Component>... types) {
        Objects.requireNonNull(types, "types");

        return new ComponentQuery(this, List.of(types));
    }

    private Map<Class<? extends Component>, Component> requireEntity(Entity entity) {
        Map<Class<? extends Component>, Component> result = components.get(entity);

        if (result == null) {
            throw new IllegalArgumentException("Unknown entity: " + entity);
        }

        return result;
    }

    List<Map.Entry<Entity, Map<Class<? extends Component>, Component>>> componentEntries() {
        return List.copyOf(components.entrySet());
    }

    public boolean exists(Entity entity) {
        Objects.requireNonNull(entity, "entity");

        return components.containsKey(entity);
    }

    public void destroy(Entity entity) {
        Objects.requireNonNull(entity, "entity");

        if (!components.containsKey(entity)) {
            throw new IllegalArgumentException("Unknown entity: " + entity);
        }
    }
}