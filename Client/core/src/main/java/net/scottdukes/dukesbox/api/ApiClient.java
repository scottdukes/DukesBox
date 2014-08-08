package net.scottdukes.dukesbox.api;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;

import net.scottdukes.dukesbox.IDukesBoxConfig;
import net.scottdukes.dukesbox.ObservableHttpClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.util.functions.Func1;

public class ApiClient {
    private static final Gson GSON = new Gson();
    private final Logger mLogger = LoggerFactory.getLogger(ApiClient.class);
    private final IDukesBoxConfig mConfig;
    private final ObservableHttpClient mHttpClient;
    private final Func1<String, SyncManifest> mapJsonStringToManifest = new Func1<String, SyncManifest>() {
        @Override
        public SyncManifest call(String json) {
            return GSON.fromJson(json, SyncManifest.class);
        }
    };

    @Inject
    public ApiClient(IDukesBoxConfig config, ObservableHttpClient httpClient) {
        mConfig = config;
        mHttpClient = httpClient;
    }

    String getSyncUrl() {
        return mConfig.getBaseUrl() + "api/sync";
    }

    public Observable<SyncContext> getSyncContext() {
        return mHttpClient.downloadString(getSyncUrl())
                .map(new Func1<String, SyncContext>() {
                    @Override
                    public SyncContext call(String json) {
                        return GSON.fromJson(json, SyncContext.class);
                    }
                });
    }

    public void getSyncContext(final EventBus eventBus) {
        getSyncContext().subscribe(createPostToEventBusSubscriber(eventBus));
    }


    public Observable<SyncManifest> getManifestDelta(final String url, final SyncManifest currentManifest) {
        return mHttpClient.postJson(url, GSON.toJson(currentManifest))
                .map(mapJsonStringToManifest);
    }

    public Observable<SyncManifest> getManifest(final String url) {
        return mHttpClient.downloadString(url).map(mapJsonStringToManifest);
    }

    public Observable<File> downloadFile(final String url, final File targetFile) {
        return mHttpClient.downloadFile(url, targetFile);
    }


    private Observer<? super Object> createPostToEventBusSubscriber(final EventBus eventBus) {
        return new Observer<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mLogger.error("Download error", e);
            }

            @Override
            public void onNext(Object context) {
                eventBus.post(context);
            }
        };
    }
}
