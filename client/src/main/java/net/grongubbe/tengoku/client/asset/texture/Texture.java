package net.grongubbe.tengoku.client.asset.texture;

import net.grongubbe.tengoku.client.asset.image.ImageData;

public final class Texture {
    private final ImageData image;

    public Texture(ImageData image) {
        this.image = image;
    }

    public ImageData image() {
        return image;
    }
}
