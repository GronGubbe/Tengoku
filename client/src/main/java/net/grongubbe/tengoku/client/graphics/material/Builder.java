package net.grongubbe.tengoku.client.graphics.material;

public abstract class Builder<T extends Material> {
    protected abstract void validate();

    public abstract T build();

    protected abstract long hash();
}
