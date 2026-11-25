package net.grongubbe.tengoku.client.options;

import com.google.gson.*;
import net.grongubbe.tengoku.common.io.FileIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class OptionsManager {
    private static final Logger LOGGER = LogManager.getLogger(OptionsManager.class);

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Path path;

    public OptionsManager(Path path) {
        setPath(path);
        read();
    }

    public void setPath(Path path) {
        this.path = Objects.requireNonNull(path, "path");
    }

    public void read() {
        String json;

        try {
            json = FileIO.readString(path);
        } catch (NoSuchFileException e) {
            LOGGER.info("Options file not found at {}. Creating default options.", path);
            write();
            return;
        } catch (Exception e) {
            LOGGER.error("Failed to read options from {}", path, e);
            throw new RuntimeException("Failed to read options", e);
        }


        JsonObject jsonObject;

        try {
            jsonObject = JsonParser.parseString(json).getAsJsonObject();
        } catch (JsonParseException | IllegalStateException e) {
            LOGGER.warn("Invalid options file at {}. Resetting options to defaults.", path, e);

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