package net.scottdukes.dukesbox.api;

public class SyncDir {
    private String tag;
    private String local;
    private String manifest;

    public String getTag() {
        return tag;
    }

    public String getLocalRootDir() {
        return local;
    }

    public String getManifestUrl() {
        return manifest;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", tag, local);
    }
}
