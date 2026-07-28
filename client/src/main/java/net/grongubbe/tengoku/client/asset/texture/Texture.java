package net.grongubbe.tengoku.client.asset.texture;

import net.grongubbe.tengoku.client.asset.Asset;
import net.grongubbe.tengoku.client.asset.image.ImageData;

public final class Texture implements Asset, AutoCloseable {
    private final TextureKey key;
    private final ImageData image;

    public Texture(TextureKey key, ImageData image) {
        this.key = key;
        this.image = image;
    }

    @Override
    public TextureKey key() {
        return key;
    }

    public ImageData image() {
        return image;
    }

    @Override
    public void close() {
        image.close();
    }

    @Override
    public String toString() {
        return "Texture[" + key.path() + "]";
    }
}