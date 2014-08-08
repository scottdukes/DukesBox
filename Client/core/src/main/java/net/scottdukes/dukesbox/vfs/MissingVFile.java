package net.scottdukes.dukesbox.vfs;

public class MissingVFile implements VFile {
    private final String mPath;

    public MissingVFile(String path) {
        mPath = path;
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public String getHash() {
        return "";
    }

    @Override
    public long getSize() {
        return -1;
    }
}
