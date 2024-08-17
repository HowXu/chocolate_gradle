package cn.howxu.chocolate_gradle.util.net;

public class AssetObject {
    private final String hash;
    private final long size;

    public AssetObject(String hash, long size) {
        this.hash = hash;
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public long getSize() {
        return size;
    }
}
