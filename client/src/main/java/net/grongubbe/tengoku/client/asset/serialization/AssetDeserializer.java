package net.grongubbe.tengoku.client.asset.serialization;

import java.io.IOException;
import java.io.InputStream;

public interface AssetDeserializer<T> {
    T deserialize(InputStream input) throws IOException;
}
