package net.grongubbe.tengoku.client.asset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public final class AssetStore {
    private static final Logger LOGGER = LogManager.getLogger(AssetStore.class);
    private final Map<AssetId<?>, Asset> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Asset> T get(AssetId<T> id) {
        Asset existing = cache.get(id);

        if (existing != null) {
            LOGGER.info("Returning cached asset: {}", id);
            return (T) existing;
        }

        T created = id.create(this);
        cache.put(id, created);

        LOGGER.info("Created new asset: {}", id);
        return created;
    }
}