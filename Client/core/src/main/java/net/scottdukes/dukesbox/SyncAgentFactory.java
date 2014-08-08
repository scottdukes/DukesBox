package net.scottdukes.dukesbox;

import com.google.inject.Injector;

import net.scottdukes.dukesbox.api.SyncDir;
import net.scottdukes.dukesbox.vfs.ExternalFileProvider;

import javax.inject.Inject;

public class SyncAgentFactory {
    private final Injector mInjector;

    @Inject
    public SyncAgentFactory(Injector injector) {
        mInjector = injector;
    }

    public SyncAgent create(SyncDir syncDir, ExternalFileProvider fileProvider) {
        SyncAgent syncAgent = new SyncAgent(syncDir, fileProvider);
        mInjector.injectMembers(syncAgent);
        return syncAgent;
    }
}
