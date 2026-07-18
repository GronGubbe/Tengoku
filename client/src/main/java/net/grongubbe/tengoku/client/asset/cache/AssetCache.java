package net.grongubbe.tengoku.client.asset.cache;

import net.grongubbe.tengoku.client.asset.AssetKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AssetCache {
    // TODO: Use Map<AssetKey<?>, CompletableFuture<Object>> for asynchronous asset loading
    private final Map<AssetKey<?>, Object> assets = new ConcurrentHashMap<>();

    public <A> A get(AssetKey<A> key) {
        Object asset = assets.get(key);

        if (asset == null) {
            return null;
        }

        return key.type().cast(asset);
    }

    public <A> void put(AssetKey<A> key, A asset) {
        assets.put(key, asset);
    }
}
