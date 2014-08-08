package net.scottdukes.dukesbox;

import android.util.Log;

import com.google.common.eventbus.EventBus;
import com.google.common.io.Files;

import net.scottdukes.dukesbox.api.ApiClient;
import net.scottdukes.dukesbox.api.SyncDir;
import net.scottdukes.dukesbox.api.SyncFile;
import net.scottdukes.dukesbox.api.SyncManifest;
import net.scottdukes.dukesbox.vfs.ExternalFileProvider;
import net.scottdukes.dukesbox.vfs.ExternalVFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import rx.Observer;

public class SyncAgent {
    private final Logger mLogger = LoggerFactory.getLogger(SyncAgent.class);

    private final SyncDir mSyncDir;
    private final ExternalFileProvider mFileProvider;
    private ApiClient mApiClient;
    private AppPaths mAppPaths;
    private IDukesBoxConfig mConfig;
    private Timer mTimer;
    private boolean isBusy = false;
    private AtomicInteger mLatch;
    private EventBus mBus;

    public SyncAgent(SyncDir syncDir, ExternalFileProvider fileProvider) {
        mSyncDir = syncDir;
        mFileProvider = fileProvider;
    }

    @Inject
    public void setApiClient(ApiClient value) {
        mApiClient = value;
    }

    @Inject
    public void setAppPaths(AppPaths appPaths) {
        mAppPaths = appPaths;
    }

    @Inject
    public void setConfig(IDukesBoxConfig config) {
        mConfig = config;
    }

    @Inject
    public void setEventBus(EventBus bus) {
        mBus = bus;
    }

    public void start() {
        mTimer = new Timer(mSyncDir.getTag());
        int delay = 250;
        long period = TimeUnit.SECONDS.toMillis(mConfig.getPollPeriodInSeconds());
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isBusy) {
                    return;
                }
                updateManifest();
            }
        }, delay, period);
    }

    public void stop() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    private void updateManifest() {
        isBusy = true;
        mApiClient.getManifest(mSyncDir.getManifestUrl())
                .subscribe(new Observer<SyncManifest>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLogger.error("Error getting manifest delta " + mSyncDir.getManifestUrl(), e);
                    }

                    @Override
                    public void onNext(SyncManifest manifest) {
                        onManifest(manifest);
                    }
                });
    }

    public void onManifest(final SyncManifest manifest) {
        mLogger.info("Got manifest {}", manifest);

        final String tag = this.mSyncDir.getTag();

        List<SyncFile> filesToDownload = new LinkedList<SyncFile>();
        Log.d("Timing", "Begin file checks for " + tag);
        for (SyncFile file : manifest.getFiles()) {
            if (!mFileProvider.exists(file.getPath(), file.getHash())) {
                filesToDownload.add(file);
            }
        }
        Log.d("Timing", "End file checks for " + tag);

        if (filesToDownload.size() == 0) {
            isBusy = false;
            return;
        }

        mLatch = new AtomicInteger(0);

        final SyncProgress progress = new SyncProgress(tag, filesToDownload.size());
        mBus.post(progress);

        for (SyncFile file : filesToDownload) {
            final ExternalVFile vFile = mFileProvider.createFile(file.getPath());

            final String url = manifest.getBaseUri() + '/' + file.getPath();
            mLogger.debug(String.format("Creating %s from %s", vFile, url));

            final File targetFile = vFile.getFile();
            File tempFile = null;
            try {
                tempFile = createTempFile();
            } catch (IOException e) {
                mLogger.error("Could not create temp file.", e);
            }

            mLogger.info(String.format("Downloading %s to %s", url, targetFile));
            mLatch.incrementAndGet();
            mApiClient.downloadFile(url, tempFile).subscribe(new Observer<File>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                    progress.increment();
                    mBus.post(progress);
                    mLogger.error("Error downloading " + url, e);
                    checkDownloadCount();
                }

                @Override
                public void onNext(File downloadedFile) {
                    try {
                        Files.createParentDirs(targetFile);
                        Files.move(downloadedFile, targetFile);
                        progress.increment();
                        mBus.post(progress);
                    } catch (IOException e) {
                        mLogger.error("Error writing " + vFile, e);
                    }
                    checkDownloadCount();
                }
            });
        }
    }

    private void checkDownloadCount() {
        if (mLatch.decrementAndGet() == 0) {
            isBusy = false;
        }
    }

    private File createTempFile() throws IOException {
        return File.createTempFile("temp_", "_handled.raw", mAppPaths.getCacheDir());
    }
}
