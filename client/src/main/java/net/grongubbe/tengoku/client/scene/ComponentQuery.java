package net.grongubbe.tengoku.client.scene;

import java.util.*;
import java.util.function.Consumer;

public final class ComponentQuery {
    private final World world;
    private final List<Class<? extends Component>> types;

    ComponentQuery(World world, List<Class<? extends Component>> types) {
        this.world = Objects.requireNonNull(world);
        this.types = types;
    }

    public void forEach(Consumer<ComponentView> consumer) {
        Objects.requireNonNull(consumer, "consumer");

        for (Map.Entry<Entity, Map<Class<? extends Component>, Component>> entry : world.componentEntries()) {
            Map<Class<? extends Component>, Component> components = entry.getValue();


            if (!matches(components)) {
                continue;
            }

            consumer.accept(new ComponentView(entry.getKey(), components));
        }
    }

    public Optional<ComponentView> findFirst() {
        for (Map.Entry<Entity, Map<Class<? extends Component>, Component>> entry : world.componentEntries()) {
            Map<Class<? extends Component>, Component> components = entry.getValue();

            if (!matches(components)) {
                continue;
            }

            return Optional.of(new ComponentView(entry.getKey(), components));
        }

        return Optional.empty();
    }

    private boolean matches(Map<Class<? extends Component>, Component> components) {
        for (Class<? extends Component> type : types) {
            if (!components.containsKey(type)) {
                return false;
            }
        }

        return true;
    }
}