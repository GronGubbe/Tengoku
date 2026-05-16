package net.grongubbe.tengoku.common.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * A container object which may or may not contain a non-null value of type {@code T} that implements {@link Closeable}.
 * This class is similar to {@link Optional} but is designed to work with resources that need to be closed.
 * <p>
 * If a value is present, {@link #isPresent()} will return {@code true} and {@link #get()} will return the value. <br>
 * If no value is present, {@link #isPresent()} will return {@code false} and {@link #get()} will throw an {@link IllegalStateException}.
 *
 * @see Optional
 * @see AutoCloseable
 * @param <T> the type of the wrapped resource, which must implement {@link Closeable}
 */
@SuppressWarnings("unused")
public final class OptionalAutoCloseable<T extends Closeable> implements AutoCloseable {
    private final T value;
    private final boolean present;

    private OptionalAutoCloseable(T value, boolean present) {
        this.value = present ? Objects.requireNonNull(value) : null;
        this.present = present;
    }

    /**
     * Return an {@link #OptionalAutoCloseable} with the specified typed value.
     *
     * @param value the value of the resource to be wrapped, which can be null.
     * @param <T> the type of the wrapped resource, which must implement {@link Closeable}.
     * @return an {@link #OptionalAutoCloseable} with the value present if non-null, otherwise an empty container.
     */
    public static <T extends Closeable> OptionalAutoCloseable<T> ofNullable(T value) {
        return new OptionalAutoCloseable<>(value, value != null);
    }

    /**
     * Return an empty {@link #OptionalAutoCloseable} with no value present.
     *
     * @param <T> the type of the (not present) wrapped resource, which must implement {@link Closeable}.
     * @return an empty {@link #OptionalAutoCloseable} with no value present.
     */
    public static <T extends Closeable> OptionalAutoCloseable<T> empty() {
        return new OptionalAutoCloseable<>(null, false);
    }

    /**
     * Indicates whether a value is present.
     *
     * @return {@code true} if a value is present, otherwise {@code false}
     */
    public boolean isPresent() {
        return present;
    }

    /**
     * Indicates whether a value is not present.
     *
     * @return {@code true} if no value is present, otherwise {@code false}
     */
    public boolean isEmpty() {
        return !present;
    }

    /**
     * Get the value if present.
     *
     * @return the non-null value held by this container.
     * @throws IllegalStateException if no value is present.
     */
    public T get() throws IllegalStateException {
        if (!present) {
            throw new IllegalStateException("No value present");
        }

        return value;
    }

    /**
     * Get an {@link Optional} containing the value if present, if not, an empty {@link Optional}.
     *
     * @return {@link Optional} with the value if present, empty if not.
     */
    public Optional<T> getOptional() {
        return Optional.ofNullable(value);
    }

    /**
     * Close the resource if present. If no value is present, this method is a no-op.
     *
     * @throws IOException if an I/O error occurs when closing the resource.
     */
    @Override
    public void close() throws IOException {
        if (present && value != null) {
            value.close();
        }
    }
}
