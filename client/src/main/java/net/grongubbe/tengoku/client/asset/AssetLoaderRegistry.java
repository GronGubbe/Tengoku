package net.grongubbe.tengoku.client.asset;

import java.util.HashMap;
import java.util.Map;

public final class AssetLoaderRegistry {
    private final Map<Class<?>, AssetLoader<?, ?>> loaders = new HashMap<>();

    public <K extends AssetKey<A>, A extends Asset> void register(Class<K> keyType, AssetLoader<K, A> loader) {
        loaders.put(keyType, loader);
    }

    @SuppressWarnings("unchecked")
    public <K extends AssetKey<A>, A extends Asset> AssetLoader<K, A> get(K key) {
        return (AssetLoader<K, A>) loaders.get(key.getClass());
    }
}