package net.scottdukes.dukesbox.vfs;

public interface VFile {
    String getPath();

    String getHash();

    long getSize();
}
