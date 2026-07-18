package net.grongubbe.tengoku.client.asset.material;

import net.grongubbe.tengoku.client.asset.shader.MaterialParameterDefinition;
import net.grongubbe.tengoku.client.asset.shader.ShaderLayout;

public final class MaterialValueBuffer {
    private final ShaderLayout layout;
    private final Object[] values;

    public MaterialValueBuffer(ShaderLayout layout) {
        this.layout = layout;
        this.values = new Object[layout.size()];
    }

    public void set(int slot, Object value) {
        MaterialParameterDefinition parameter = layout.parameter(slot);

        if (value != null && !parameter.type().valueType().isInstance(value)) {
            throw new IllegalArgumentException(
                    "Invalid value for material parameter \"" +
                            parameter.name() +
                            "\". Expected " +
                            parameter.type().valueType().getSimpleName() +
                            ", received " +
                            value.getClass().getSimpleName()
            );
        }

        values[slot] = value;
    }

    public void set(String parameterName, Object value) {
        int slot = layout.parameter(parameterName).slot();
        set(slot, value);
    }

    public Object get(int slot) {
        return values[slot];
    }
}
