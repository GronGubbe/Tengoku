package net.grongubbe.tengoku.client.input.glfw;

import net.grongubbe.tengoku.client.input.*;
import net.grongubbe.tengoku.client.render.RenderThread;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public final class GlfwInput implements Input, Keyboard, Mouse {
    private final boolean[] currentKeys = new boolean[Key.values().length];
    private final boolean[] pressedKeys = new boolean[Key.values().length];
    private final boolean[] releasedKeys = new boolean[Key.values().length];

    private final boolean[] pendingPressedKeys = new boolean[Key.values().length];
    private final boolean[] pendingReleasedKeys = new boolean[Key.values().length];

    private final boolean[] currentMouseButtons = new boolean[MouseButton.values().length];
    private final boolean[] pressedMouseButtons = new boolean[MouseButton.values().length];
    private final boolean[] releasedMouseButtons = new boolean[MouseButton.values().length];

    private final boolean[] pendingPressedMouseButtons = new boolean[MouseButton.values().length];
    private final boolean[] pendingReleasedMouseButtons = new boolean[MouseButton.values().length];

    private double mouseX;
    private double mouseY;

    private double mouseDeltaX;
    private double mouseDeltaY;

    private double pendingMouseDeltaX;
    private double pendingMouseDeltaY;

    public GlfwInput(long window) {
        RenderThread.assertCurrent();

        double[] cursorX = new double[1];
        double[] cursorY = new double[1];

        glfwGetCursorPos(window, cursorX, cursorY);

        mouseX = cursorX[0];
        mouseY = cursorY[0];

        glfwSetKeyCallback(window, (_, key, _, action, _) -> {
            Key mappedKey = mapKey(key);

            if (mappedKey == null) {
                return;
            }

            int index = mappedKey.ordinal();

            if (action == GLFW_PRESS) {
                currentKeys[index] = true;
                pendingPressedKeys[index] = true;
            } else if (action == GLFW_RELEASE) {
                currentKeys[index] = false;
                pendingReleasedKeys[index] = true;
            }
        });

        glfwSetMouseButtonCallback(window, (_, button, action, _) -> {
            MouseButton mappedButton = mapMouseButton(button);

            if (mappedButton == null) {
                return;
            }

            int index = mappedButton.ordinal();

            if (action == GLFW_PRESS) {
                currentMouseButtons[index] = true;
                pendingPressedMouseButtons[index] = true;
            } else if (action == GLFW_RELEASE) {
                currentMouseButtons[index] = false;
                pendingReleasedMouseButtons[index] = true;
            }
        });

        glfwSetCursorPosCallback(window, (_, x, y) -> {
            pendingMouseDeltaX += x - mouseX;
            pendingMouseDeltaY += y - mouseY;

            mouseX = x;
            mouseY = y;
        });
    }

    public void update() {
        RenderThread.assertCurrent();

        System.arraycopy(pendingPressedKeys, 0, pressedKeys, 0, pendingPressedKeys.length);
        System.arraycopy(pendingReleasedKeys, 0, releasedKeys, 0, pendingReleasedKeys.length);
        System.arraycopy(pendingPressedMouseButtons, 0, pressedMouseButtons, 0, pendingPressedMouseButtons.length);
        System.arraycopy(pendingReleasedMouseButtons, 0, releasedMouseButtons, 0, pendingReleasedMouseButtons.length);

        Arrays.fill(pendingPressedKeys, false);
        Arrays.fill(pendingReleasedKeys, false);
        Arrays.fill(pendingPressedMouseButtons, false);
        Arrays.fill(pendingReleasedMouseButtons, false);

        mouseDeltaX = pendingMouseDeltaX;
        mouseDeltaY = pendingMouseDeltaY;

        pendingMouseDeltaX = 0.0;
        pendingMouseDeltaY = 0.0;
    }

    @Override
    public Keyboard keyboard() {
        return this;
    }

    @Override
    public Mouse mouse() {
        return this;
    }

    @Override
    public boolean isDown(Key key) {
        return currentKeys[key.ordinal()];
    }

    @Override
    public boolean wasPressed(Key key) {
        return pressedKeys[key.ordinal()];
    }

    @Override
    public boolean wasReleased(Key key) {
        return releasedKeys[key.ordinal()];
    }

    @Override
    public boolean isDown(MouseButton button) {
        return currentMouseButtons[button.ordinal()];
    }

    @Override
    public boolean wasPressed(MouseButton button) {
        return pressedMouseButtons[button.ordinal()];
    }

    @Override
    public boolean wasReleased(MouseButton button) {
        return releasedMouseButtons[button.ordinal()];
    }

    @Override
    public double x() {
        return mouseX;
    }

    @Override
    public double y() {
        return mouseY;
    }

    @Override
    public double deltaX() {
        return mouseDeltaX;
    }

    @Override
    public double deltaY() {
        return mouseDeltaY;
    }

    private static Key mapKey(int key) {
        return switch (key) {
            case GLFW_KEY_SPACE -> Key.SPACE;
            case GLFW_KEY_APOSTROPHE -> Key.APOSTROPHE;
            case GLFW_KEY_COMMA -> Key.COMMA;
            case GLFW_KEY_MINUS -> Key.MINUS;
            case GLFW_KEY_PERIOD -> Key.PERIOD;
            case GLFW_KEY_SLASH -> Key.SLASH;

            case GLFW_KEY_0 -> Key.NUM_0;
            case GLFW_KEY_1 -> Key.NUM_1;
            case GLFW_KEY_2 -> Key.NUM_2;
            case GLFW_KEY_3 -> Key.NUM_3;
            case GLFW_KEY_4 -> Key.NUM_4;
            case GLFW_KEY_5 -> Key.NUM_5;
            case GLFW_KEY_6 -> Key.NUM_6;
            case GLFW_KEY_7 -> Key.NUM_7;
            case GLFW_KEY_8 -> Key.NUM_8;
            case GLFW_KEY_9 -> Key.NUM_9;

            case GLFW_KEY_SEMICOLON -> Key.SEMICOLON;
            case GLFW_KEY_EQUAL -> Key.EQUAL;

            case GLFW_KEY_A -> Key.A;
            case GLFW_KEY_B -> Key.B;
            case GLFW_KEY_C -> Key.C;
            case GLFW_KEY_D -> Key.D;
            case GLFW_KEY_E -> Key.E;
            case GLFW_KEY_F -> Key.F;
            case GLFW_KEY_G -> Key.G;
            case GLFW_KEY_H -> Key.H;
            case GLFW_KEY_I -> Key.I;
            case GLFW_KEY_J -> Key.J;
            case GLFW_KEY_K -> Key.K;
            case GLFW_KEY_L -> Key.L;
            case GLFW_KEY_M -> Key.M;
            case GLFW_KEY_N -> Key.N;
            case GLFW_KEY_O -> Key.O;
            case GLFW_KEY_P -> Key.P;
            case GLFW_KEY_Q -> Key.Q;
            case GLFW_KEY_R -> Key.R;
            case GLFW_KEY_S -> Key.S;
            case GLFW_KEY_T -> Key.T;
            case GLFW_KEY_U -> Key.U;
            case GLFW_KEY_V -> Key.V;
            case GLFW_KEY_W -> Key.W;
            case GLFW_KEY_X -> Key.X;
            case GLFW_KEY_Y -> Key.Y;
            case GLFW_KEY_Z -> Key.Z;

            case GLFW_KEY_LEFT_BRACKET -> Key.LEFT_BRACKET;
            case GLFW_KEY_BACKSLASH -> Key.BACKSLASH;
            case GLFW_KEY_RIGHT_BRACKET -> Key.RIGHT_BRACKET;
            case GLFW_KEY_GRAVE_ACCENT -> Key.GRAVE_ACCENT;

            case GLFW_KEY_ESCAPE -> Key.ESCAPE;
            case GLFW_KEY_ENTER -> Key.ENTER;
            case GLFW_KEY_TAB -> Key.TAB;
            case GLFW_KEY_BACKSPACE -> Key.BACKSPACE;
            case GLFW_KEY_INSERT -> Key.INSERT;
            case GLFW_KEY_DELETE -> Key.DELETE;

            case GLFW_KEY_RIGHT -> Key.RIGHT;
            case GLFW_KEY_LEFT -> Key.LEFT;
            case GLFW_KEY_DOWN -> Key.DOWN;
            case GLFW_KEY_UP -> Key.UP;

            case GLFW_KEY_PAGE_UP -> Key.PAGE_UP;
            case GLFW_KEY_PAGE_DOWN -> Key.PAGE_DOWN;
            case GLFW_KEY_HOME -> Key.HOME;
            case GLFW_KEY_END -> Key.END;

            case GLFW_KEY_CAPS_LOCK -> Key.CAPS_LOCK;
            case GLFW_KEY_SCROLL_LOCK -> Key.SCROLL_LOCK;
            case GLFW_KEY_NUM_LOCK -> Key.NUM_LOCK;
            case GLFW_KEY_PRINT_SCREEN -> Key.PRINT_SCREEN;
            case GLFW_KEY_PAUSE -> Key.PAUSE;

            case GLFW_KEY_F1 -> Key.F1;
            case GLFW_KEY_F2 -> Key.F2;
            case GLFW_KEY_F3 -> Key.F3;
            case GLFW_KEY_F4 -> Key.F4;
            case GLFW_KEY_F5 -> Key.F5;
            case GLFW_KEY_F6 -> Key.F6;
            case GLFW_KEY_F7 -> Key.F7;
            case GLFW_KEY_F8 -> Key.F8;
            case GLFW_KEY_F9 -> Key.F9;
            case GLFW_KEY_F10 -> Key.F10;
            case GLFW_KEY_F11 -> Key.F11;
            case GLFW_KEY_F12 -> Key.F12;

            case GLFW_KEY_KP_0 -> Key.KP_0;
            case GLFW_KEY_KP_1 -> Key.KP_1;
            case GLFW_KEY_KP_2 -> Key.KP_2;
            case GLFW_KEY_KP_3 -> Key.KP_3;
            case GLFW_KEY_KP_4 -> Key.KP_4;
            case GLFW_KEY_KP_5 -> Key.KP_5;
            case GLFW_KEY_KP_6 -> Key.KP_6;
            case GLFW_KEY_KP_7 -> Key.KP_7;
            case GLFW_KEY_KP_8 -> Key.KP_8;
            case GLFW_KEY_KP_9 -> Key.KP_9;

            case GLFW_KEY_KP_DECIMAL -> Key.KP_DECIMAL;
            case GLFW_KEY_KP_DIVIDE -> Key.KP_DIVIDE;
            case GLFW_KEY_KP_MULTIPLY -> Key.KP_MULTIPLY;
            case GLFW_KEY_KP_SUBTRACT -> Key.KP_SUBTRACT;
            case GLFW_KEY_KP_ADD -> Key.KP_ADD;
            case GLFW_KEY_KP_ENTER -> Key.KP_ENTER;
            case GLFW_KEY_KP_EQUAL -> Key.KP_EQUAL;

            case GLFW_KEY_LEFT_SHIFT -> Key.LEFT_SHIFT;
            case GLFW_KEY_LEFT_CONTROL -> Key.LEFT_CONTROL;
            case GLFW_KEY_LEFT_ALT -> Key.LEFT_ALT;
            case GLFW_KEY_LEFT_SUPER -> Key.LEFT_SUPER;
            case GLFW_KEY_RIGHT_SHIFT -> Key.RIGHT_SHIFT;
            case GLFW_KEY_RIGHT_CONTROL -> Key.RIGHT_CONTROL;
            case GLFW_KEY_RIGHT_ALT -> Key.RIGHT_ALT;
            case GLFW_KEY_RIGHT_SUPER -> Key.RIGHT_SUPER;

            case GLFW_KEY_MENU -> Key.MENU;

            default -> null;
        };
    }

    private static MouseButton mapMouseButton(int button) {
        return switch (button) {
            case GLFW_MOUSE_BUTTON_LEFT -> MouseButton.LEFT;
            case GLFW_MOUSE_BUTTON_RIGHT -> MouseButton.RIGHT;
            case GLFW_MOUSE_BUTTON_MIDDLE -> MouseButton.MIDDLE;
            case GLFW_MOUSE_BUTTON_4 -> MouseButton.BUTTON_4;
            case GLFW_MOUSE_BUTTON_5 -> MouseButton.BUTTON_5;
            case GLFW_MOUSE_BUTTON_6 -> MouseButton.BUTTON_6;
            case GLFW_MOUSE_BUTTON_7 -> MouseButton.BUTTON_7;
            case GLFW_MOUSE_BUTTON_8 -> MouseButton.BUTTON_8;
            default -> null;
        };
    }
}