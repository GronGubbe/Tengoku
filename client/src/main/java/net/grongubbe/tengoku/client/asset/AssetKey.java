package net.grongubbe.tengoku.client.asset;

public interface AssetKey<A extends Asset> {
    Class<A> type();
}