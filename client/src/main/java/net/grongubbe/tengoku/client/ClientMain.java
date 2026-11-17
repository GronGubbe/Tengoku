package net.grongubbe.tengoku.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientMain {
    private static final Logger LOGGER = LogManager.getLogger(ClientMain.class);

    static void main() {
        try (Bootstrapper bootstrapper = new Bootstrapper()) {
            bootstrapper.init();
            bootstrapper.boot();
        } catch (Exception e) {
            LOGGER.error("Fatal error in main", e);
        }
    }
}
