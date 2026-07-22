package net.grongubbe.tengoku.client.asset.texture;

import net.grongubbe.tengoku.client.asset.image.ImageData;

public final class Texture implements AutoCloseable {
    private final ImageData image;

    public Texture(ImageData image) {
        this.image = image;
    }

    public ImageData image() {
        return image;
    }

    @Override
    public void close() {
        image.close();
    }
}
