package net.grongubbe.tengoku.client.input;

import net.grongubbe.tengoku.client.input.binding.AxisBinding;
import net.grongubbe.tengoku.client.input.binding.InputBinding;

import java.util.*;

public final class InputMap {
    private final Map<Action, Set<InputBinding>> actionBindings = new HashMap<>();
    private final Map<Axis, Set<AxisBinding>> axisBindings = new HashMap<>();

    public void bind(Action action, InputBinding binding) {
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(binding, "binding");

        actionBindings.computeIfAbsent(action, _ -> new HashSet<>()).add(binding);
    }

    public void unbind(Action action, InputBinding binding) {
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(binding, "binding");

        Set<InputBinding> bindings = actionBindings.get(action);

        if (bindings == null) {
            return;
        }

        bindings.remove(binding);

        if (bindings.isEmpty()) {
            actionBindings.remove(action);
        }
    }

    public void clear(Action action) {
        Objects.requireNonNull(action, "action");

        actionBindings.remove(action);
    }

    public Set<InputBinding> bindings(Action action) {
        Objects.requireNonNull(action, "action");

        Set<InputBinding> bindings = actionBindings.get(action);

        if (bindings == null) {
            return Set.of();
        }

        return Collections.unmodifiableSet(bindings);
    }

    public void bind(Axis axis, AxisBinding binding) {
        Objects.requireNonNull(axis, "axis");
        Objects.requireNonNull(binding, "binding");

        axisBindings.computeIfAbsent(axis, _ -> new HashSet<>()).add(binding);
    }

    public void unbind(Axis axis, AxisBinding binding) {
        Objects.requireNonNull(axis, "axis");
        Objects.requireNonNull(binding, "binding");

        Set<AxisBinding> bindings = axisBindings.get(axis);

        if (bindings == null) {
            return;
        }

        bindings.remove(binding);

        if (bindings.isEmpty()) {
            axisBindings.remove(axis);
        }
    }

    public void clear(Axis axis) {
        Objects.requireNonNull(axis, "axis");

        axisBindings.remove(axis);
    }

    public Set<AxisBinding> bindings(Axis axis) {
        Objects.requireNonNull(axis, "axis");

        Set<AxisBinding> bindings = axisBindings.get(axis);

        if (bindings == null) {
            return Set.of();
        }

        return Collections.unmodifiableSet(bindings);
    }
}