package net.scottdukes.dukesbox.api;

import net.scottdukes.dukesbox.vfs.VFile;

public class SyncFile implements VFile {
    private String path;
    private String hash;
    private long size;

    public SyncFile() {
    }

    /**
     * Creates an instance by cloning an existing VFile instance
     *
     * @param file The VFile instance to clone.
     */
    public SyncFile(VFile file) {
        this();
        this.path = file.getPath();
        this.hash = file.getHash();
        this.size = file.getSize();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String value) {
        this.path = value;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String value) {
        this.hash = value;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long value) {
        this.size = value;
    }

    @Override
    public String toString() {
        return path;
    }
}
