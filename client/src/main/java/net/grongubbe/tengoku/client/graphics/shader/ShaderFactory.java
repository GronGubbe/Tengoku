package net.grongubbe.tengoku.client.graphics.shader;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ShaderFactory {
    Class<? extends ShaderDescriptor> value();
}