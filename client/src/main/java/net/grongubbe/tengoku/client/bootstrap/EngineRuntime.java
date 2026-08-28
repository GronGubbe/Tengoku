package net.grongubbe.tengoku.client.bootstrap;

import net.grongubbe.tengoku.client.core.ClientServices;

public final class EngineRuntime implements Lifecycle {
    private ClientServices services;

    @Override
    public void start() {
        if (services != null) {
            throw new IllegalStateException("Engine runtime has already started");
        }

        services = new ClientServices();
        services.initialize();
    }

    @Override
    public void stop() {
        if (services == null) {
            return;
        }

        services.gpuResources().cleanup();
        services = null;
    }

    public ClientServices services() {
        if (services == null) {
            throw new IllegalStateException("Engine runtime has not started");
        }

        return services;
    }
}