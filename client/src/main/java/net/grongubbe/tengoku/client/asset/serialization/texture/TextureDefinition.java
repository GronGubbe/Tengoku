package net.grongubbe.tengoku.client.asset.serialization.texture;

import java.nio.file.Path;

public final class TextureDefinition {
    private final Path image;

    public TextureDefinition(Path image) {
        this.image = image;
    }

    public Path image() {
        return image;
    }
}