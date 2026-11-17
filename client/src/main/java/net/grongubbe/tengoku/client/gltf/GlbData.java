package net.grongubbe.tengoku.client.gltf;

record GlbData(String json, byte[] binaryData) {
    GlbData {
        binaryData = binaryData.clone();
    }

    @Override
    public byte[] binaryData() {
        return binaryData.clone();
    }
}