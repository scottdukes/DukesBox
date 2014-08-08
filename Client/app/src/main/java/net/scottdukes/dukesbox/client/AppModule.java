package net.scottdukes.dukesbox.client;

import android.os.Process;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;

import net.scottdukes.dukesbox.AppPaths;
import net.scottdukes.dukesbox.IDukesBoxConfig;
import net.scottdukes.dukesbox.ObservableHttpClient;
import net.scottdukes.dukesbox.ObservableHttpClientImpl;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import roboguice.inject.SharedPreferencesName;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EventBus.class).toInstance(new EventBus("Global"));

        bind(ThreadPoolExecutor.class).toInstance(getDefaultThreadPoolExecutor());

        bind(IDukesBoxConfig.class).to(DukesBoxConfig.class);
        bind(AppPaths.class).to(AppPathsImpl.class);
        bind(ObservableHttpClient.class).to(ObservableHttpClientImpl.class);

        bindConstant()
                .annotatedWith(SharedPreferencesName.class)
                .to("net.scottdukes.dukesbox");

        //bind(DownloadManager.class).toProvider(new SystemServiceProvider<DownloadManager>(Context.DOWNLOAD_SERVICE));
    }

    private ThreadPoolExecutor getDefaultThreadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        r.run();
                    }
                });
            }
        });
    }
}
