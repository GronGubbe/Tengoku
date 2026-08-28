package net.grongubbe.tengoku.client;

import net.grongubbe.tengoku.client.bootstrap.Bootstrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ClientMain {
    private static final Logger LOGGER = LogManager.getLogger(ClientMain.class);

    static void main() {
        try {
            Bootstrapper.launch();
        } catch (Throwable throwable) {
            LOGGER.error("Fatal client error", throwable);
        } finally {
            LOGGER.info("Tengoku stopped");
        }
    }
}