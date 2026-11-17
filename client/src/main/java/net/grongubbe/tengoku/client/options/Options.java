package net.grongubbe.tengoku.client.options;

import java.util.Map;

public class Options {
    public static final Option<Integer> WINDOW_WIDTH = new Option<>("windowWidth", 1280);
    public static final Option<Integer> WINDOW_HEIGHT = new Option<>("windowHeight", 720);
    public static final Option<Integer> TPS = new Option<>("tps", 20);
    public static final Option<Boolean> FULLSCREEN = new Option<>("fullscreen", false);
    public static final Option<Boolean> VSYNC = new Option<>("vsync", true);

    public static final Map<String, Option<?>> OPTIONS = Map.of(
            WINDOW_WIDTH.getName(), WINDOW_WIDTH,
            WINDOW_HEIGHT.getName(), WINDOW_HEIGHT,
            TPS.getName(), TPS,
            FULLSCREEN.getName(), FULLSCREEN,
            VSYNC.getName(), VSYNC
    );
}