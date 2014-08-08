package net.scottdukes.dukesbox;

import com.google.common.collect.ImmutableList;

import net.scottdukes.dukesbox.api.SyncContext;
import net.scottdukes.dukesbox.api.SyncDir;
import net.scottdukes.dukesbox.vfs.ExternalFileProvider;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class AgentManager {
    private final Map<String, SyncAgent> mAgents = new HashMap<String, SyncAgent>();
    private final Map<String, ExternalFileProvider> mFileProviders = new HashMap<String, ExternalFileProvider>();
    private final SyncAgentFactory mAgentFactory;
    private final AppPaths mAppPaths;

    @Inject
    public AgentManager(SyncAgentFactory agentFactory, AppPaths appPaths) {
        mAgentFactory = agentFactory;
        mAppPaths = appPaths;
    }


    public void update(SyncContext context) {
        for (SyncDir syncDir : context.getDirs()) {
            String localRootDir = syncDir.getLocalRootDir();

            ExternalFileProvider fileProvider = getFileProvider(localRootDir);

            String tag = syncDir.getTag();
            if (!mAgents.containsKey(tag)) {
                SyncAgent agent = mAgentFactory.create(syncDir, fileProvider);

                mAgents.put(tag, agent);

                agent.start();
            }
        }
    }

    private ExternalFileProvider getFileProvider(String localRootDir) {
        if (mFileProviders.containsKey(localRootDir)) {
            return mFileProviders.get(localRootDir);
        }

        ExternalFileProvider fileProvider = new ExternalFileProvider(mAppPaths.getExternalStorageDirectory(), localRootDir);
        mFileProviders.put(localRootDir, fileProvider);
        return fileProvider;
    }

    public void stop() {
        clearAndStopAgents();
        clearFileProviders();
    }

    private void clearFileProviders() {
        ImmutableList<ExternalFileProvider> providers = ImmutableList.copyOf(this.mFileProviders.values());
        this.mFileProviders.clear();

        for (ExternalFileProvider provider : providers) {
            provider.clearCache();
        }
    }

    private void clearAndStopAgents() {
        ImmutableList<SyncAgent> agents = ImmutableList.copyOf(this.mAgents.values());
        this.mAgents.clear();

        for (SyncAgent agent : agents) {
            agent.stop();
        }
    }
}
