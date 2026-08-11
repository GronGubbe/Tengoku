package net.grongubbe.tengoku.client.input;

import net.grongubbe.tengoku.client.input.binding.InputBinding;
import net.grongubbe.tengoku.client.input.binding.KeyBinding;
import net.grongubbe.tengoku.client.input.binding.MouseButtonBinding;

import java.util.Objects;

public final class ActionInput {
    private final Input input;
    private final InputMap inputMap;

    public ActionInput(Input input, InputMap inputMap) {
        this.input = Objects.requireNonNull(input, "input");
        this.inputMap = Objects.requireNonNull(inputMap, "inputMap");
    }

    public boolean isDown(Action action) {
        return isInputState(action, InputState.DOWN);
    }

    public boolean wasPressed(Action action) {
        return isInputState(action, InputState.PRESSED);
    }

    public boolean wasReleased(Action action) {
        return isInputState(action, InputState.RELEASED);
    }

    private boolean isInputState(Action action, InputState state) {
        Objects.requireNonNull(action, "action");

        for (InputBinding binding : inputMap.bindings(action)) {
            if (state.matches(input, binding)) {
                return true;
            }
        }

        return false;
    }

    private enum InputState {
        DOWN {
            @Override
            boolean matches(Input input, InputBinding binding) {
                return switch (binding) {
                    case KeyBinding key -> input.keyboard().isDown(key.key());
                    case MouseButtonBinding button -> input.mouse().isDown(button.button());
                };
            }
        },

        PRESSED {
            @Override
            boolean matches(Input input, InputBinding binding) {
                return switch (binding) {
                    case KeyBinding key -> input.keyboard().wasPressed(key.key());
                    case MouseButtonBinding button -> input.mouse().wasPressed(button.button());
                };
            }
        },

        RELEASED {
            @Override
            boolean matches(Input input, InputBinding binding) {
                return switch (binding) {
                    case KeyBinding key -> input.keyboard().wasReleased(key.key());
                    case MouseButtonBinding button -> input.mouse().wasReleased(button.button());
                };
            }
        };

        abstract boolean matches(Input input, InputBinding binding);
    }
}