package net.grongubbe.tengoku.client.asset.model;

import net.grongubbe.tengoku.client.asset.AssetKey;

import java.nio.file.Path;

public record ModelKey(Path path) implements AssetKey<Model> {
    @Override
    public Class<Model> type() {
        return Model.class;
    }
}