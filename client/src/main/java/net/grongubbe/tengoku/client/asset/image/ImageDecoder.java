package net.grongubbe.tengoku.client.asset.image;

import java.io.IOException;
import java.io.InputStream;

public interface ImageDecoder {
    ImageData decode(InputStream input) throws IOException;
}
