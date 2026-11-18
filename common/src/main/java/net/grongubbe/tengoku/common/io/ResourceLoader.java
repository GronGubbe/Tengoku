package net.grongubbe.tengoku.common.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class ResourceLoader {
    private static final Logger LOGGER = LogManager.getLogger(ResourceLoader.class);

    private ResourceLoader() {}

    public static InputStream openStream(String path) throws IOException {
        Objects.requireNonNull(path, "path");

        String resourceName = toResourceName(path);

        InputStream inputStream = ResourceLoader.class.getClassLoader().getResourceAsStream(resourceName);

        if (inputStream == null) {
            String message = "Classpath resource not found: " + resourceName;
            LOGGER.warn(message);
            throw new FileNotFoundException(message);
        }

        LOGGER.debug("Opened classpath resource: {}", resourceName);

        return inputStream;
    }

    public static String readString(String path) throws IOException {
        try (InputStream inputStream = openStream(path)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static byte[] readBytes(String path) throws IOException {
        try (InputStream inputStream = openStream(path)) {
            return inputStream.readAllBytes();
        }
    }

    public static Reader openReader(String path) throws IOException {
        return new InputStreamReader(openStream(path), StandardCharsets.UTF_8);
    }


    private static String toResourceName(String path) {
        String resourceName = path.replace('\\', '/');

        while (resourceName.startsWith("/")) {
            resourceName = resourceName.substring(1);
        }

        if (resourceName.isBlank()) {
            throw new IllegalArgumentException("Resource path cannot be empty.");
        }

        return resourceName;
    }
}
