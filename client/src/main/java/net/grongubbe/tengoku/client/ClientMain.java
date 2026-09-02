package net.grongubbe.tengoku.client;

import net.grongubbe.tengoku.client.bootstrap.Bootstrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ClientMain {
    private static final Logger LOGGER = LogManager.getLogger(ClientMain.class);

    static void main() {
        int exitCode = 0;

        try {
            Bootstrapper.launch();
        } catch (RuntimeException | Error exception) {
            LOGGER.error(
                    "Fatal client error:\n    {}",
                    exception.getMessage().stripTrailing().replace("\n", "\n    ")
            );
            exitCode = 1;
        } finally {
            LOGGER.info("Tengoku stopped");
        }

        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}