package net.grongubbe.tengoku.client;

import net.grongubbe.tengoku.client.core.Tengoku;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientMain {
    private final static Logger LOGGER = LogManager.getLogger(ClientMain.class);

    static void main() {
        System.out.println("Hello Client!");
        Tengoku tengoku = new Tengoku();

        try {
            tengoku.run();
        } catch (Exception e) {
            LOGGER.error("Uncaught exception in main", e);
        } finally {
            tengoku.cleanup();
        }
    }
}
