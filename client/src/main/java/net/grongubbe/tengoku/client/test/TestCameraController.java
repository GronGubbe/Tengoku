package net.grongubbe.tengoku.client.test;

import net.grongubbe.tengoku.client.input.*;
import net.grongubbe.tengoku.client.input.binding.KeyBinding;
import net.grongubbe.tengoku.client.input.binding.MouseAxis;
import net.grongubbe.tengoku.client.input.binding.MouseAxisBinding;
import net.grongubbe.tengoku.client.render.Window;
import net.grongubbe.tengoku.client.scene.components.TransformComponent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;

public final class TestCameraController {
    private static final Action MOVE_FORWARD = new Action("camera_move_forward");
    private static final Action MOVE_BACKWARD = new Action("camera_move_backward");
    private static final Action MOVE_LEFT = new Action("camera_move_left");
    private static final Action MOVE_RIGHT = new Action("camera_move_right");
    private static final Action MOVE_UP = new Action("camera_move_up");
    private static final Action MOVE_DOWN = new Action("camera_move_down");
    private static final Action QUIT = new Action("camera_quit");

    private static final Axis LOOK_X = new Axis("camera_look_x");
    private static final Axis LOOK_Y = new Axis("camera_look_y");

    private static final float MOVE_SPEED = 5.0f;
    private static final float LOOK_SENSITIVITY = 0.0025f;
    private static final float MAX_PITCH = (float) Math.toRadians(89.0);

    private final TransformComponent transform;
    private final Window window;
    private final ActionInput actionInput;
    private final AxisInput axisInput;

    private float yaw;
    private float pitch;

    private final Vector3f movement = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();
    private final Quaternionf movementRotation = new Quaternionf();

    public TestCameraController(TransformComponent transform, Window window) {
        this.transform = Objects.requireNonNull(transform, "transform");
        this.window = Objects.requireNonNull(window, "window");

        Input input = window.input();

        InputMap inputMap = new InputMap();

        inputMap.bind(QUIT, new KeyBinding(Key.ESCAPE));

        inputMap.bind(MOVE_FORWARD, new KeyBinding(Key.W));
        inputMap.bind(MOVE_BACKWARD, new KeyBinding(Key.S));
        inputMap.bind(MOVE_LEFT, new KeyBinding(Key.A));
        inputMap.bind(MOVE_RIGHT, new KeyBinding(Key.D));
        inputMap.bind(MOVE_UP, new KeyBinding(Key.SPACE));
        inputMap.bind(MOVE_DOWN, new KeyBinding(Key.LEFT_CONTROL));

        inputMap.bind(LOOK_X, new MouseAxisBinding(MouseAxis.X, 1.0f));
        inputMap.bind(LOOK_Y, new MouseAxisBinding(MouseAxis.Y, 1.0f));

        actionInput = new ActionInput(input, inputMap);
        axisInput = new AxisInput(input, inputMap);

        initializeRotation();
    }

    public void update(float deltaTime) {
        if (actionInput.wasPressed(QUIT)) {
            window.requestClose();
            return;
        }

        updateRotation();
        updateMovement(deltaTime);
    }

    private void initializeRotation() {
        Vector3f forward = new Vector3f(0.0f, 0.0f, -1.0f);

        transform.rotation(rotation);
        rotation.transform(forward);

        yaw = (float) Math.atan2(-forward.x, -forward.z);
        pitch = (float) Math.asin(Math.max(-1.0f, Math.min(1.0f, forward.y)));
    }

    private void updateRotation() {
        yaw -= axisInput.value(LOOK_X) * LOOK_SENSITIVITY;
        pitch -= axisInput.value(LOOK_Y) * LOOK_SENSITIVITY;

        pitch = Math.max(-MAX_PITCH, Math.min(MAX_PITCH, pitch));
        rotation.identity().rotateY(yaw).rotateX(pitch);
        transform.setRotation(rotation);
    }

    private void updateMovement(float deltaTime) {
        movement.zero();

        if (actionInput.isDown(MOVE_FORWARD)) {
            movement.z -= 1.0f;
        }

        if (actionInput.isDown(MOVE_BACKWARD)) {
            movement.z += 1.0f;
        }

        if (actionInput.isDown(MOVE_LEFT)) {
            movement.x -= 1.0f;
        }

        if (actionInput.isDown(MOVE_RIGHT)) {
            movement.x += 1.0f;
        }

        if (actionInput.isDown(MOVE_UP)) {
            movement.y += 1.0f;
        }

        if (actionInput.isDown(MOVE_DOWN)) {
            movement.y -= 1.0f;
        }

        if (movement.lengthSquared() == 0.0f) {
            return;
        }

        movement.normalize().mul(MOVE_SPEED * deltaTime);

        movementRotation.identity().rotateY(yaw);
        movementRotation.transform(movement);

        transform.translate(movement);
    }
}