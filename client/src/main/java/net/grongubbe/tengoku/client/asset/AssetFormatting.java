package net.grongubbe.tengoku.client.asset;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class AssetFormatting {
    private AssetFormatting() {
    }

    public static String formatValue(Object value) {
        return switch (value) {
            case null -> "null";

            case String string -> "\"" + string + "\"";

            case List<?> list -> list.stream()
                    .map(AssetFormatting::formatValue)
                    .collect(Collectors.joining(", ", "[", "]"));

            case Map<?, ?> map -> map.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(Object::toString)))
                    .map(entry -> AssetFormatting.formatValue(entry.getKey()) + ": " + AssetFormatting.formatValue(entry.getValue()))
                    .collect(Collectors.joining(", ", "{", "}"));

            default -> value.toString();
        };
    }
}