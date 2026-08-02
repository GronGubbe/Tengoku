package net.grongubbe.tengoku.client;

import net.grongubbe.tengoku.client.core.Tengoku;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ClientMain {
    private static final Logger LOGGER = LogManager.getLogger(ClientMain.class);

    static void main() {
        try (Tengoku tengoku = new Tengoku()) {
            LOGGER.info("Starting Tengoku");
            tengoku.run();
        } catch (Throwable throwable) {
            LOGGER.error("Fatal client error", throwable);
        } finally {
            LOGGER.info("Tengoku stopped");
        }
    }
}