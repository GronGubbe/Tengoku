package net.grongubbe.tengoku.client.asset;

public interface AssetLoadingContext {
    <A extends Asset> A get(AssetKey<A> key);
}