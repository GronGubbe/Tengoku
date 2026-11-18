package net.grongubbe.tengoku.client.options;

import com.google.gson.*;
import net.grongubbe.tengoku.common.io.FileIO;

import java.nio.file.Path;
import java.util.Map;

@SuppressWarnings("unchecked")
public class OptionsManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Path path;

    public OptionsManager(Path path) {
        setPath(path);
        read();
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public void read() {
        String json;

        try {
            json = FileIO.readString(path);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read options", e); // TODO: Better logging later
        }

        JsonObject jsonObject;

        try {
            jsonObject = JsonParser.parseString(json).getAsJsonObject();
        } catch (JsonParseException | IllegalStateException e) {
            for (Option<?> option : Options.OPTIONS.values()) {
                Option<Object> typedOption = (Option<Object>) option;
                typedOption.setValue(typedOption.getDefaultValue());
            }

            return;
        }

        for (Map.Entry<String, Option<?>> entry : Options.OPTIONS.entrySet()) {
            String key = entry.getKey();
            Option<?> option = entry.getValue();

            JsonElement element = jsonObject.get(key);

            if (element != null && !element.isJsonNull()) {
                Option<Object> typedOption = (Option<Object>) option;

                typedOption.setValue(gson.fromJson(element, typedOption.getDefaultValue().getClass()));
            }
        }
    }

    public void write() {
        JsonObject jsonObject = new JsonObject();

        for (Map.Entry<String, Option<?>> entry : Options.OPTIONS.entrySet()) {
            String key = entry.getKey();
            Option<?> option = entry.getValue();

            jsonObject.add(key, gson.toJsonTree(option.getValue()));
        }

        try {
            FileIO.writeString(path, gson.toJson(jsonObject));
        } catch (Exception e) {
            throw new RuntimeException("Failed to write options", e); // TODO: Better logging later
        }
    }
}