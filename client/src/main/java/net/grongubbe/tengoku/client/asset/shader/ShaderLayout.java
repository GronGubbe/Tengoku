package net.grongubbe.tengoku.client.asset.shader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ShaderLayout {
    private final List<MaterialParameterDefinition> parameters;
    private final Map<String, MaterialParameterDefinition> parametersByName;

    public ShaderLayout(List<MaterialParameterDefinition> parameters) {
        this.parameters = List.copyOf(parameters);

        for (int i = 0; i < parameters.size(); i++) {
            MaterialParameterDefinition parameter = parameters.get(i);

            if (parameter.slot() != i) {
                throw new IllegalArgumentException("Parameter slot %d does not match index %d (%s)"
                        .formatted(parameter.slot(), i, parameter.name())
                );
            }
        }

        this.parametersByName = parameters.stream()
                .collect(Collectors.toUnmodifiableMap(
                        MaterialParameterDefinition::name,
                        parameter -> parameter
                ));
    }

    public List<MaterialParameterDefinition> parameters() {
        return parameters;
    }

    public MaterialParameterDefinition parameter(int slot) {
        return parameters.get(slot);
    }

    public MaterialParameterDefinition parameter(String name) {
        return parametersByName.get(name);
    }

    public int size() {
        return parameters.size();
    }
}