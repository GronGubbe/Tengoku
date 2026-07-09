package net.grongubbe.tengoku.client.graphics.material;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class MaterialCache {
    private static final Logger LOGGER = LogManager.getLogger(MaterialCache.class);
    private static final Map<Long, Material> CACHE = new ConcurrentHashMap<>();

    public static Material get(long key, Supplier<Material> creator) {
        Material existing = CACHE.get(key);

        if (existing != null) {
            LOGGER.debug("Material cache hit [key={}]", key);
            return existing;
        }

        Material created = creator.get();
        CACHE.put(key, created);

        LOGGER.info("Material cached [key={}, type={}]", key, created.type());
        return created;
    }
}