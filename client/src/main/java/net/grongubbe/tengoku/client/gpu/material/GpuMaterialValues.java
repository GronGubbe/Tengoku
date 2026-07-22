package net.grongubbe.tengoku.client.gpu.material;

import java.util.Map;

public final class GpuMaterialValues {
    private final Map<Integer, Object> values;

    public GpuMaterialValues(Map<Integer, Object> values) {
        this.values = Map.copyOf(values);
    }

    public Object get(int slot) {
        return values.get(slot);
    }
}