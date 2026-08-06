package net.grongubbe.tengoku.client.scene;

import java.util.Map;
import java.util.Objects;

public final class ComponentView {
    private final Entity entity;
    private final Map<Class<? extends Component>, Component> components;

    ComponentView(Entity entity, Map<Class<? extends Component>, Component> components) {
        this.entity = Objects.requireNonNull(entity);
        this.components = Objects.requireNonNull(components);
    }

    public Entity entity() {
        return entity;
    }

    public <T extends Component> T get(Class<T> type) {
        Objects.requireNonNull(type, "type");

        Component component = components.get(type);

        if (component == null) {
            throw new IllegalArgumentException("Entity does not contain component: " + type.getSimpleName());
        }

        return type.cast(component);
    }
}