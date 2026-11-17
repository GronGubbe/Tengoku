package net.grongubbe.tengoku.client;

public class Bootstrapper implements AutoCloseable {
    private final Tengoku tengoku;

    public Bootstrapper() {
        tengoku = new Tengoku();
    }

    public void init() {
        tengoku.init();
    }

    public void boot() {
        tengoku.run();
    }

    @Override
    public void close() {
        tengoku.close();
    }
}
