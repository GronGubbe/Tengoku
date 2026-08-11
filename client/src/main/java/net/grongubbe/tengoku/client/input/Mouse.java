package net.grongubbe.tengoku.client.input;

public interface Mouse {
    boolean isDown(MouseButton button);

    boolean wasPressed(MouseButton button);

    boolean wasReleased(MouseButton button);

    double x();

    double y();

    double deltaX();

    double deltaY();
}