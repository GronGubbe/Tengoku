package net.grongubbe.tengoku.common.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public final class FileIO {
    private FileIO() {}

    public static InputStream openInputStream(String path) throws IOException {
        Objects.requireNonNull(path, "path");
        return openInputStream(Path.of(path));
    }

    public static InputStream openInputStream(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        return Files.newInputStream(path);
    }

    public static InputStream openInputStream(File file) throws IOException {
        Objects.requireNonNull(file, "file");
        return openInputStream(file.toPath());
    }

    public static Reader openReader(String path) throws IOException {
        Objects.requireNonNull(path, "path");
        return openReader(Path.of(path));
    }

    public static Reader openReader(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        return Files.newBufferedReader(path);
    }

    public static Reader openReader(File path) throws IOException {
        Objects.requireNonNull(path, "path");
        return openReader(path.toPath());
    }

    public static byte[] readBytes(String path) throws IOException {
        Objects.requireNonNull(path, "path");
        return readBytes(Path.of(path));
    }

    public static byte[] readBytes(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        return Files.readAllBytes(path);
    }

    public static byte[] readBytes(File path) throws IOException {
        Objects.requireNonNull(path, "path");
        return readBytes(path.toPath());
    }

    public static String readString(String path) throws IOException {
        Objects.requireNonNull(path, "path");
        return readString(Path.of(path));
    }

    public static String readString(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        return Files.readString(path);
    }

    public static String readString(File path) throws IOException {
        Objects.requireNonNull(path, "path");
        return readString(path.toPath());
    }

    public static OutputStream openOutputStream(String path) throws IOException {
        Objects.requireNonNull(path, "path");
        return openOutputStream(Path.of(path));
    }

    public static OutputStream openOutputStream(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        return Files.newOutputStream(path);
    }

    public static OutputStream openOutputStream(File path) throws IOException {
        Objects.requireNonNull(path, "path");
        return openOutputStream(path.toPath());
    }

    public static Writer openWriter(String path) throws IOException {
        Objects.requireNonNull(path, "path");
        return openWriter(Path.of(path));
    }

    public static Writer openWriter(Path path) throws IOException {
        Objects.requireNonNull(path, "path");
        return Files.newBufferedWriter(path);
    }

    public static Writer openWriter(File path) throws IOException {
        Objects.requireNonNull(path, "path");
        return openWriter(path.toPath());
    }

    public static void writeBytes(String path, byte[] data) throws IOException {
        Objects.requireNonNull(path, "path");
        writeBytes(Path.of(path), data);
    }

    public static void writeBytes(Path path, byte[] data) throws IOException {
        Objects.requireNonNull(path, "path");
        Files.write(path, data);
    }

    public static void writeBytes(File path, byte[] data) throws IOException {
        Objects.requireNonNull(path, "path");
        writeBytes(path.toPath(), data);
    }

    public static void writeString(String path, String data) throws IOException {
        Objects.requireNonNull(path, "path");
        writeString(Path.of(path), data);
    }

    public static void writeString(Path path, String data) throws IOException {
        Objects.requireNonNull(path, "path");
        Files.writeString(path, data);
    }

    public static void writeString(File path, String data) throws IOException {
        Objects.requireNonNull(path, "path");
        writeString(path.toPath(), data);
    }

    public static void appendString(String path, String data) throws IOException {
        Objects.requireNonNull(path, "path");
        appendString(Path.of(path), data);
    }

    public static void appendString(Path path, String data) throws IOException {
        Objects.requireNonNull(path, "path");
        Files.writeString(path, data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public static void appendString(File path, String data) throws IOException {
        Objects.requireNonNull(path, "path");
        appendString(path.toPath(), data);
    }
}
