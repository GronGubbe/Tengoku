package net.grongubbe.tengoku.common.io;

import net.grongubbe.tengoku.common.util.OptionalAutoCloseable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class with static methods to load resources from classpath or filesystem.
 */
@SuppressWarnings("unused")
public final class ResourceLoader {
    private static final Logger LOGGER = Logger.getLogger(ResourceLoader.class.getName());

    private ResourceLoader() {}

    /**
     * Open resource from classpath or filesystem.
     * <p>
     * Search order: <br>
     *  - If {@code path} is absolute and exists, filesystem is tried first. <br>
     *  - Otherwise classpath is tried first and then filesystem.
     *
     * @param path resource path (not null)
     * @return InputStream of opened resource
     * @throws FileNotFoundException if resource not found
     * @throws IOException if an I/O error occurs
     */
    public static InputStream open(String path) throws FileNotFoundException, IOException {
        Objects.requireNonNull(path, "path");
        return open(Path.of(path));
    }

    /**
     * Open resource from classpath or filesystem.
     * <p>
     * Search order: <br>
     *  - If {@code path} is absolute and exists, filesystem is tried first. <br>
     *  - Otherwise classpath is tried first and then filesystem.
     *
     * @param path resource path (not null)
     * @return InputStream of opened resource
     * @throws FileNotFoundException if resource not found
     * @throws IOException if an I/O error occurs
     */
    public static InputStream open(Path path) throws FileNotFoundException, IOException {
        Objects.requireNonNull(path, "path");

        // If the provided Path is an absolute filesystem path that exists, open it first.
        if (path.isAbsolute() && Files.exists(path)) {
            LOGGER.log(Level.FINE, "Opened filesystem resource: " + path.toAbsolutePath());
            return Files.newInputStream(path);
        }

        // Try classpath
        String resourceName = toResourceName(path);
        InputStream in = ResourceLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (in != null) {
            LOGGER.log(Level.FINE, "Opened classpath resource: " + resourceName);
            return in;
        }

        // Fallback to relative filesystem path
        if (Files.exists(path)) {
            LOGGER.log(Level.FINE, "Opened filesystem resource: " + path.toAbsolutePath());
            return Files.newInputStream(path);
        }

        String msg = "Resource not found on classpath or filesystem: " + path;
        LOGGER.log(Level.WARNING, msg);
        throw new FileNotFoundException(msg);
    }

    /**
     * Open resource from classpath or filesystem.
     * <p>
     * Search order: <br>
     *  - If {@code path} is absolute and exists, filesystem is tried first. <br>
     *  - Otherwise classpath is tried first and then filesystem.
     * <p>
     * This method differs from {@link #open(String)} in that it does not throw
     * when the resource is absent. Instead, it returns an empty {@link OptionalAutoCloseable}.
     *
     * @param path resource path (not null)
     * @return OptionalAutoCloseable containing an InputStream if found, empty if not found.
     * @throws IOException if an I/O error occurs
     */
    public static OptionalAutoCloseable<InputStream> tryOpen(String path) throws IOException {
        Objects.requireNonNull(path, "path");
        return tryOpen(Path.of(path));
    }

    /**
     * Open resource from classpath or filesystem.
     * <p>
     * Search order: <br>
     *  - If {@code path} is absolute and exists, filesystem is tried first. <br>
     *  - Otherwise classpath is tried first and then filesystem.
     * <p>
     * This method differs from {@link #open(Path)} in that it does not throw
     * when the resource is not found. Instead, it returns an empty {@link OptionalAutoCloseable}.
     *
     * @param path resource path (not null)
     * @return OptionalAutoCloseable containing an InputStream if found, empty if not found.
     * @throws IOException if an I/O error occurs
     */
    public static OptionalAutoCloseable<InputStream> tryOpen(Path path) throws IOException {
        Objects.requireNonNull(path, "path");

        // If absolute and exists, open filesystem resource
        if (path.isAbsolute() && Files.exists(path)) {
            LOGGER.log(Level.FINE, "Opened filesystem resource: " + path.toAbsolutePath());
            return OptionalAutoCloseable.ofNullable(Files.newInputStream(path));
        }

        // Try classpath
        String resourceName = toResourceName(path);
        InputStream in = ResourceLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (in != null) {
            LOGGER.log(Level.FINE, "Opened classpath resource: " + resourceName);
            return OptionalAutoCloseable.ofNullable(in);
        }

        // Fallback to relative filesystem path
        if (Files.exists(path)) {
            LOGGER.log(Level.FINE, "Opened filesystem resource: " + path.toAbsolutePath());
            return OptionalAutoCloseable.ofNullable(Files.newInputStream(path));
        }

        return OptionalAutoCloseable.empty();
    }

    /**
     * Read resource as UTF-8 string from classpath or filesystem.
     * <p>
     * Search order: <br>
     *  - If {@code path} is absolute and exists, filesystem is tried first. <br>
     *  - Otherwise classpath is tried first and then filesystem.
     *
     * @param path resource path (not null)
     * @return content of resource as string
     * @throws FileNotFoundException if resource not found
     * @throws IOException if an I/O error occurs
     */
    public static String readString(String path) throws FileNotFoundException, IOException {
        Objects.requireNonNull(path, "path");
        return readString(Path.of(path));
    }

    /**
     * Read resource as UTF-8 string from classpath or filesystem.
     * <p>
     * Search order: <br>
     *  - If {@code path} is absolute and exists, filesystem is tried first. <br>
     *  - Otherwise classpath is tried first and then filesystem.
     *
     * @param path resource path (not null)
     * @return content of resource as string
     * @throws FileNotFoundException if resource not found
     * @throws IOException if an I/O error occurs
     */
    public static String readString(Path path) throws FileNotFoundException, IOException {
        try (InputStream in = open(path)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Open resource from classpath or filesystem as a UTF-8 {@link Reader}.
     * <p>
     * Search order: <br>
     *  - If {@code path} is absolute and exists, filesystem is tried first. <br>
     *  - Otherwise classpath is tried first and then filesystem.
     *
     * @param path resource path (not null)
     * @return Reader of opened resource
     * @throws FileNotFoundException if resource not found
     * @throws IOException if an I/O error occurs
     */
    public static Reader openReader(String path) throws FileNotFoundException, IOException {
        Objects.requireNonNull(path, "path");
        return openReader(Path.of(path));
    }

    /**
     * Open resource from classpath or filesystem as a UTF-8 {@link Reader}.
     * <p>
     * Search order: <br>
     *  - If {@code path} is absolute and exists, filesystem is tried first. <br>
     *  - Otherwise classpath is tried first and then filesystem.
     *
     * @param path resource path (not null)
     * @return Reader of opened resource
     * @throws FileNotFoundException if resource not found
     * @throws IOException if an I/O error occurs
     */
    public static Reader openReader(Path path) throws FileNotFoundException, IOException {
        Objects.requireNonNull(path, "path");
        return new InputStreamReader(open(path), StandardCharsets.UTF_8);
    }

    /**
     * Open resource from classpath or filesystem as a UTF-8 {@link Reader}.
     * <p>
     * Search order: <br>
     *  - If {@code path} is absolute and exists, filesystem is tried first. <br>
     *  - Otherwise classpath is tried first and then filesystem.
     * <p>
     * This method differs from {@link #openReader(String)} in that it does not throw
     * when the resource is absent. Instead, it returns an empty {@link OptionalAutoCloseable}.
     *
     * @param path resource path (not null)
     * @return OptionalAutoCloseable containing a Reader if found, empty if not found.
     * @throws IOException if an I/O error occurs
     */
    public static OptionalAutoCloseable<Reader> tryOpenReader(String path) throws IOException {
        Objects.requireNonNull(path, "path");
        return tryOpenReader(Path.of(path));
    }

    /**
     * Open resource from classpath or filesystem as a UTF-8 {@link Reader}.
     * <p>
     * Search order: <br>
     *  - If {@code path} is absolute and exists, filesystem is tried first. <br>
     *  - Otherwise classpath is tried first and then filesystem.
     * <p>
     * This method differs from {@link #openReader(Path)} in that it does not throw
     * when the resource is not found. Instead, it returns an empty {@link OptionalAutoCloseable}.
     *
     * @param path resource path (not null)
     * @return OptionalAutoCloseable containing a Reader if found, empty if not found.
     * @throws IOException if an I/O error occurs
     */
    public static OptionalAutoCloseable<Reader> tryOpenReader(Path path) throws IOException {
        Objects.requireNonNull(path, "path");

        // If absolute and exists, open filesystem resource
        if (path.isAbsolute() && Files.exists(path)) {
            LOGGER.log(Level.FINE, "Opened filesystem resource (reader): " + path.toAbsolutePath());
            return OptionalAutoCloseable.ofNullable(Files.newBufferedReader(path, StandardCharsets.UTF_8));
        }

        // Try classpath
        String resourceName = toResourceName(path);
        InputStream in = ResourceLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (in != null) {
            LOGGER.log(Level.FINE, "Opened classpath resource (reader): " + resourceName);
            return OptionalAutoCloseable.ofNullable(new InputStreamReader(in, StandardCharsets.UTF_8));
        }

        // Fallback to relative filesystem path
        if (Files.exists(path)) {
            LOGGER.log(Level.FINE, "Opened filesystem resource (reader): " + path.toAbsolutePath());
            return OptionalAutoCloseable.ofNullable(Files.newBufferedReader(path, StandardCharsets.UTF_8));
        }

        return OptionalAutoCloseable.empty();
    }

    /**
     * Read resource from classpath or filesystem as a {@link Properties}.
     * <p>
     * Search order: <br>
     *  - If {@code path} is absolute and exists, filesystem is tried first. <br>
     *  - Otherwise classpath is tried first and then filesystem.
     *
     * @param path resource path (not null)
     * @return Properties loaded from the resource
     * @throws FileNotFoundException if resource not found
     * @throws IOException if an I/O error occurs
     */
    public static Properties readProperties(Path path) throws FileNotFoundException, IOException {
        Objects.requireNonNull(path, "path");
        try (Reader reader = openReader(path)) {
            Properties props = new Properties();
            props.load(reader);
            return props;
        }
    }

    /**
     * Read resource from classpath or filesystem as a {@link Properties}.
     * <p>
     * Search order: <br>
     *  - If {@code path} is absolute and exists, filesystem is tried first. <br>
     *  - Otherwise classpath is tried first and then filesystem.
     *
     * @param path resource path (not null)
     * @return Properties loaded from the resource
     * @throws FileNotFoundException if resource not found
     * @throws IOException if an I/O error occurs
     */
    public static Properties readProperties(String path) throws FileNotFoundException, IOException {
        return readProperties(Path.of(path));
    }

    // Convert Path to resource name suitable for classloader (slashes instead of backslashes, no leading slash)
    private static String toResourceName(Path path) {
        String s = path.normalize().toString().replace(File.separatorChar, '/');
        if (s.startsWith("/")) {
            s = s.substring(1);
        }
        return s;
    }
}
