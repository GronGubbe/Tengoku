package net.grongubbe.tengoku.client.graphics.shader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Set;

public class ShaderBootstrapper {
    private static final Logger LOGGER = LogManager.getLogger(ShaderBootstrapper.class);

    public static void scanAndRegister() {
        LOGGER.info("Scanning for shader implementations...");
        Reflections reflections = new Reflections("net.grongubbe.tengoku");
        Set<Class<?>> shaders = reflections.getTypesAnnotatedWith(ShaderFactory.class);

        LOGGER.info("Found {} shader implementations", shaders.size());
        for (Class<?> shaderClass : shaders) {
            ShaderFactory annotation = shaderClass.getAnnotation(ShaderFactory.class);

            if (annotation == null) {
                continue;
            }

            Class<? extends ShaderDescriptor> descriptorType = annotation.value();
            registerShader(shaderClass, descriptorType);
        }
    }

    private static void registerShader(Class<?> shaderClass, Class<? extends ShaderDescriptor> descriptorType) {
        LOGGER.info("Registering shader [class={}, descriptor={}]", shaderClass.getSimpleName(), descriptorType.getSimpleName());
        ShaderManager.register(
                descriptorType,
                descriptor -> {
                    try {
                        Constructor<?> ctor = shaderClass.getDeclaredConstructor(descriptorType);
                        ctor.setAccessible(true);
                        return (Shader) ctor.newInstance(descriptor);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to construct shader: " + shaderClass.getName(), e);
                    }
                }
        );
    }
}