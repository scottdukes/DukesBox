package net.scottdukes.dukesbox.api;

import net.scottdukes.dukesbox.vfs.VFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SyncManifest {
    private String baseUri;
    private List<SyncFile> files = new ArrayList<SyncFile>();

    public String getBaseUri() {
        return baseUri;
    }

    public Collection<SyncFile> getFiles() {
        return files;
    }

    @Override
    public String toString() {
        return String.format("%s: files %d", baseUri, files.size());
    }

    public void addAll(Iterable<VFile> iterable) {
        for (VFile file : iterable) {
            files.add(new SyncFile(file));
        }
    }
}
