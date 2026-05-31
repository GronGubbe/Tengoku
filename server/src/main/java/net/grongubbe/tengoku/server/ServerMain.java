package net.grongubbe.tengoku.server;

import net.grongubbe.tengoku.common.util.io.ResourceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class ServerMain {
    private final static Logger LOGGER = LogManager.getLogger(ServerMain.class);

    static void main() {
        System.out.println("Hello Server!");

        try {
            @SuppressWarnings("unused")
            Properties properties = ResourceLoader.readProperties("preLoad/server.properties");
        } catch (Exception e) {
            LOGGER.error("Uncaught exception reached final ");
        }
    }
}
