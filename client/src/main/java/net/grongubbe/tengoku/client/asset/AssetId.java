package net.grongubbe.tengoku.client.asset;

public interface AssetId<T extends Asset> {
    T create(AssetStore assets);
}