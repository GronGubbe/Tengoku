package net.grongubbe.tengoku.client.input;

import net.grongubbe.tengoku.client.input.binding.AxisBinding;
import net.grongubbe.tengoku.client.input.binding.KeyAxisBinding;
import net.grongubbe.tengoku.client.input.binding.MouseAxis;
import net.grongubbe.tengoku.client.input.binding.MouseAxisBinding;

import java.util.Objects;

public final class AxisInput {
    private final Input input;
    private final InputMap inputMap;

    public AxisInput(Input input, InputMap inputMap) {
        this.input = Objects.requireNonNull(input, "input");
        this.inputMap = Objects.requireNonNull(inputMap, "inputMap");
    }

    public float value(Axis axis) {
        Objects.requireNonNull(axis, "axis");

        float value = 0.0f;

        for (AxisBinding binding : inputMap.bindings(axis)) {
            value += value(binding);
        }

        return value;
    }

    private float value(AxisBinding binding) {
        return (float) switch (binding) {
            case KeyAxisBinding key -> input.keyboard().isDown(key.key()) ? key.value() : 0.0f;
            case MouseAxisBinding mouse -> mouseDelta(mouse.axis()) * mouse.scale();
        };
    }

    private double mouseDelta(MouseAxis axis) {
        return switch (axis) {
            case X -> input.mouse().deltaX();
            case Y -> input.mouse().deltaY();
        };
    }
}