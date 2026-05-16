package net.grongubbe.tengoku.client;

public final class Time {
    private final double fixedDelta;
    private long lastTime;
    private double accumulator;

    public Time(double tickRate) {
        this.fixedDelta = 1.0 / tickRate;
    }

    public void start() {
        lastTime = System.nanoTime();
    }

    public void beginFrame() {
        long now = System.nanoTime();
        double frameTime = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;

        accumulator += Math.min(frameTime, 0.25);
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
}