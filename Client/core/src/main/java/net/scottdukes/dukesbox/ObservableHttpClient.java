package net.scottdukes.dukesbox;

import java.io.File;

import rx.Observable;

public interface ObservableHttpClient {
    Observable<String> downloadString(String url);

    Observable<String> postJson(String url, String json);

    Observable<File> downloadFile(String url, File targetFile);
}
