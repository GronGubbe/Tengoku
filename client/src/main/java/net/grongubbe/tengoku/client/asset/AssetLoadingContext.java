package net.grongubbe.tengoku.client.asset;

public interface AssetLoadingContext {
    <A> A get(AssetKey<A> key);
}
