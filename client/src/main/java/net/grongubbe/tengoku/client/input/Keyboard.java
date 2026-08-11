package net.grongubbe.tengoku.client.input;

public interface Keyboard {
    boolean isDown(Key key);

    boolean wasPressed(Key key);

    boolean wasReleased(Key key);
}