package net.grongubbe.tengoku.client.asset;

import net.grongubbe.tengoku.client.asset.assets.Utils.Assets;

public abstract class Builder<T extends Asset> {
    protected abstract void validate();

    public abstract T build(Assets assets);
}