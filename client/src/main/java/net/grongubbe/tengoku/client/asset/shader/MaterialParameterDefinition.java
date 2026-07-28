package net.grongubbe.tengoku.client.asset.shader;

public final class MaterialParameterDefinition {
    private final int slot;
    private final String name;
    private final MaterialParameterType type;
    private final Object defaultValue;

    public MaterialParameterDefinition(int slot, String name, MaterialParameterType type, Object defaultValue) {
        this.slot = slot;
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;

        if (defaultValue != null && !type.valueType().isInstance(defaultValue)) {
            throw new IllegalArgumentException("Default value is not of expected type " + type.valueType().getSimpleName());
        }
    }

    public int slot() {
        return slot;
    }

    public String name() {
        return name;
    }

    public MaterialParameterType type() {
        return type;
    }

    public Object defaultValue() {
        return defaultValue;
    }
}