package net.grongubbe.tengoku.client.bootstrap;

public final class Bootstrapper {
    private Bootstrapper() {
    }

    public static void launch() {
        PlatformRuntime platform = new PlatformRuntime(1024, 512, "Tengoku", false, true);
        EngineRuntime engine = new EngineRuntime();
        GameRuntime game = new GameRuntime(platform, engine.assetRuntime(), engine.renderRuntime());

        try (Application application = new Application(platform, engine, game)) {
            application.run();
        }
    }
}