package net.grongubbe.tengoku.client.graphics.shader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class ShaderManager {
    private static final Logger LOGGER = LogManager.getLogger(ShaderManager.class);
    private static final Map<Key<?>, Shader> CACHE = new HashMap<>();
    private static final Map<Class<? extends ShaderDescriptor>, Function<ShaderDescriptor, Shader>> FACTORIES = new HashMap<>();

    public static void register(Class<? extends ShaderDescriptor> descriptorType, Function<? extends ShaderDescriptor, ? extends Shader> factory) {
        FACTORIES.put(descriptorType, (Function<ShaderDescriptor, Shader>) factory);
        LOGGER.info("Shader factory registered [type={}]", descriptorType.getSimpleName());
    }

    public static <D extends ShaderDescriptor> Shader get(Class<D> type, D descriptor) {
        Function<ShaderDescriptor, Shader> factory = FACTORIES.get(type);

        if (factory == null) {
            throw new IllegalStateException("No shader factory registered for " + type);
        }

        Key<D> key = new Key<>(type, descriptor);

        Shader cached = CACHE.get(key);
        if (cached != null) {
            LOGGER.debug("Shader cache hit [type={}]", type.getSimpleName());
            return cached;
        }

        LOGGER.info("Creating shader [type={}]", type.getSimpleName());
        Shader created = factory.apply(descriptor);
        CACHE.put(key, created);
        LOGGER.info("Shader cached [type={}]", type.getSimpleName());
        return created;
    }

    private record Key<D extends ShaderDescriptor>(Class<D> type, D descriptor) {}
}