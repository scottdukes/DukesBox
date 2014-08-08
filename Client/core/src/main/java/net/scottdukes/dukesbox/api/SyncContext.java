package net.scottdukes.dukesbox.api;

import java.util.Collection;
import java.util.List;

public class SyncContext {
    private List<SyncDir> dirs;

    public Collection<SyncDir> getDirs() {
        return dirs;
    }
}
