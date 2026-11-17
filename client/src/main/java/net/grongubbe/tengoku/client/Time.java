package net.grongubbe.tengoku.client;

public final class Time {
    private static final double NANOS_PER_SECOND = 1_000_000_000.0;
    private static final long STATS_INTERVAL = 1_000_000_000L;
    private static final double MAX_DELTA_TIME = 0.25;

    private final int targetTps;
    private final double fixedDeltaTime;

    private boolean initialized;

    private long lastTime;
    private double deltaTime;
    private double accumulator;

    private long frameCount;
    private long tickCount;

    private int fps;
    private int tps;

    private long framesSinceLastStats;
    private long ticksSinceLastStats;
    private long statsStartTime;

    private boolean frameStarted;

    public Time(int targetTps) {
        if (targetTps <= 0) {
            throw new IllegalArgumentException("Target TPS must be greater than zero.");
        }

        this.targetTps = targetTps;
        this.fixedDeltaTime = 1.0 / targetTps;
    }

    public void startFrame() {
        if (frameStarted) {
            throw new IllegalStateException("Cannot start a frame before ending the previous frame.");
        }

        if (!initialized) {
            this.lastTime = System.nanoTime();
            this.statsStartTime = System.nanoTime();

            initialized = true;
        }

        long currentTimeNanos = System.nanoTime();

        long elapsedNanos = currentTimeNanos - lastTime;

        lastTime = currentTimeNanos;

        deltaTime = elapsedNanos / NANOS_PER_SECOND;
        deltaTime = Math.min(deltaTime, MAX_DELTA_TIME);

        accumulator += deltaTime;

        frameStarted = true;
    }

    public boolean shouldTick() {
        if (!frameStarted) {
            throw new IllegalStateException("No frame is currently active.");
        }

        return accumulator >= fixedDeltaTime;
    }

    public void completeTick() {
        if (!shouldTick()) {
            throw new IllegalStateException("Cannot complete a tick when no tick is due.");
        }

        accumulator -= fixedDeltaTime;

        tickCount++;
        ticksSinceLastStats++;
    }

    public void endFrame() {
        if (!frameStarted) {
            throw new IllegalStateException("Cannot end a frame before starting a frame.");
        }

        frameCount++;
        framesSinceLastStats++;

        updateStatistics(System.nanoTime());

        frameStarted = false;
    }

    private void updateStatistics(long currentTimeNanos) {
        long elapsedNanos = currentTimeNanos - statsStartTime;

        if (elapsedNanos >= STATS_INTERVAL) {
            fps = (int) Math.round(framesSinceLastStats * (NANOS_PER_SECOND / elapsedNanos));
            tps = (int) Math.round(ticksSinceLastStats * (NANOS_PER_SECOND / elapsedNanos));

            framesSinceLastStats = 0L;
            ticksSinceLastStats = 0L;

            statsStartTime = currentTimeNanos;
        }
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public double getFixedDeltaTime() {
        return fixedDeltaTime;
    }

    public int getTargetTps() {
        return targetTps;
    }

    public int getFps() {
        return fps;
    }

    public int getTps() {
        return tps;
    }

    public long getFrameCount() {
        return frameCount;
    }

    public long getTickCount() {
        return tickCount;
    }
}
