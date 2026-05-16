package net.grongubbe.tengoku.client;

public final class Time {
    private final double fixedDelta;

    private long lastTime;
    private double accumulator;

    private double fpsTimer;
    private int fpsFrames;
    private int fps;

    public Time(double tickRate) {
        this.fixedDelta = 1.0 / tickRate;
    }

    public void start() {
        lastTime = System.nanoTime();
        fpsTimer = 0.0;
        fpsFrames = 0;
        fps = 0;
    }

    public void beginFrame() {
        long now = System.nanoTime();
        double frameTime = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;

        accumulator += Math.min(frameTime, 0.25);

        fpsTimer += frameTime;
        fpsFrames++;

        if (fpsTimer >= 1.0) {
            fps = (int) Math.round(fpsFrames / fpsTimer);
            fpsTimer -= 1.0;
            fpsFrames = 0;
        }
    }

    public boolean shouldUpdate() {
        return accumulator >= fixedDelta;
    }

    public void consumeUpdate() {
        accumulator -= fixedDelta;
    }

    public double alphaTime() {
        return Math.max(0.0, Math.min(1.0, accumulator / fixedDelta));
    }

    public double fixedDelta() {
        return fixedDelta;
    }

    public int fps() {
        return fps;
    }
}