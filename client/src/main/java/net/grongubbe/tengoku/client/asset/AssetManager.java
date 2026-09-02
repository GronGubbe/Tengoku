package net.grongubbe.tengoku.client.asset;

import net.grongubbe.tengoku.client.asset.cache.AssetCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public final class AssetManager implements AssetLoadingContext {
    private static final Logger LOGGER = LogManager.getLogger(AssetManager.class);

    private final AssetCache cache;
    private final AssetLoaderRegistry loaders;

    public AssetManager(AssetCache cache, AssetLoaderRegistry loaders) {
        this.cache = cache;
        this.loaders = loaders;
    }

    @Override
    public <A extends Asset> A get(AssetKey<A> key) {
        A cached = cache.get(key);

        if (cached != null) {
            LOGGER.debug("Returning cached asset {}", key);
            return cached;
        }

        AssetLoader<AssetKey<A>, A> loader = loaders.get(key);

        if (loader == null) {
            throw new IllegalStateException("No loader registered for asset key: " + key.getClass().getName());
        }

        LOGGER.debug("Loading asset {}", key);

        try {
            A asset = loader.load(key, this);
            cache.put(key, asset);

            LOGGER.debug("Loaded asset {}", key);

            return asset;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load asset '" + key + "': " + exception.getMessage(), exception);
        }
    }
}