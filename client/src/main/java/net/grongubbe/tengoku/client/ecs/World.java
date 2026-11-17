package net.grongubbe.tengoku.client.ecs;

import java.util.*;

public class World {
    private int nextId = 0;

    private final Set<Integer> entities = new HashSet<>();

    private final Map<Class<? extends IComponent>, ComponentStore<? extends IComponent>> stores = new HashMap<>();

    public Entity createEntity() {
        int id = nextId++;
        entities.add(id);
        return new Entity(id);
    }

    public Entity getEntity(int id) {
        if (entityExists(id)) {
            return new Entity(id);
        }

        return null;
    }

    public boolean entityExists(int id) {
        return entities.contains(id);
    }

    public void removeEntity(Entity entity) {
        if (entityExists(entity.id())) {
            List<IComponent> components = getComponents(entity);

            for (IComponent component : components) {
                removeComponent(entity, component.getClass());
            }

            entities.remove(entity.id());
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends IComponent> void addComponent(Entity entity, T component) {
        if (entityExists(entity.id())) {
            ComponentStore<T> store = (ComponentStore<T>) stores.get(component.getClass());

            if (store == null) {
                store = new ComponentStore<>();
                stores.put(component.getClass(), store);
            }

            store.add(entity.id(), component);
        }
    }

    public List<IComponent> getComponents(Entity entity) {
        List<IComponent> result = new ArrayList<>();

        if (entityExists(entity.id())) {
            for (ComponentStore<? extends IComponent> store : stores.values()) {
                IComponent component = store.get(entity.id());
                if (component != null) {
                    result.add(component);
                }
            }
        }

        return result;
    }

    public <T extends IComponent> T getComponent(Entity entity, Class<T> component) {
        if (entityExists(entity.id())) {
            ComponentStore<?> store = stores.get(component);

            if (store != null) {
                return component.cast(store.get(entity.id()));
            }
        }

        return null;
    }

    public <T extends IComponent> void removeComponent(Entity entity, Class<T> component) {
        if (entityExists(entity.id())) {
            ComponentStore<?> store = stores.get(component);

            if (store != null) {
                store.remove(entity.id());
            }
        }
    }
}