package net.grongubbe.tengoku.client.asset;

import java.io.IOException;

public interface AssetLoader<K extends AssetKey<A>, A> {
    A load(K key, AssetLoadingContext context) throws IOException;
}