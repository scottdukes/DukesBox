package net.scottdukes.dukesbox;

import android.content.Context;

import com.google.common.io.CharStreams;
import com.google.common.io.Closer;
import com.google.common.io.Files;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Inject;

import rx.Observable;

public class ObservableHttpClientImpl implements ObservableHttpClient {
    public static final String CONTENT_TYPE_JSON = "application/json";
    private static final String UTF_8 = "UTF-8";

    private final ThreadPoolExecutor mThreadPoolExecutor;
    private final OkHttpClient mClient;

    @Inject
    private Context mContext;

    @Inject
    public ObservableHttpClientImpl(ThreadPoolExecutor threadPoolExecutor) {
        mThreadPoolExecutor = threadPoolExecutor;
        mClient = new OkHttpClient();
    }

    @Override
    public Observable<String> downloadString(final String url) {
        Future<String> future = mThreadPoolExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return downloadStringInternal(url);
            }
        });

        return Observable.from(future);
    }

    @Override
    public Observable<String> postJson(final String url, final String json) {
        Future<String> future = mThreadPoolExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return postInternal(url, json, CONTENT_TYPE_JSON);
            }
        });

        return Observable.from(future);
    }

    @Override
    public Observable<File> downloadFile(final String url, final File targetFile) {
        Future<File> future = mThreadPoolExecutor.submit(new Callable<File>() {
            @Override
            public File call() throws Exception {
                return downloadFileInternal(url, targetFile);
            }
        });

        return Observable.from(future);
    }

    private String downloadStringInternal(final String url) throws IOException {
        HttpURLConnection connection = mClient.open(new URL(url));

        Closer closer = Closer.create();
        try {
            // Read the response.
            InputStream in = closer.register(connection.getInputStream());
            return CharStreams.toString(new InputStreamReader(in, UTF_8));
        } catch (Throwable e) { // must catch Throwable
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    private String postInternal(final String url, final String body, String contentType) throws IOException {

        HttpURLConnection connection = mClient.open(new URL(url));

        Closer closer = Closer.create();
        try {
            // Write the request.
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");

            OutputStream out = closer.register(connection.getOutputStream());
            out.write(body.getBytes(UTF_8));
            out.close();

            // Read the response.
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Unexpected HTTP response: "
                        + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            InputStream in = closer.register(connection.getInputStream());
            return CharStreams.toString(new InputStreamReader(in, UTF_8));
        } catch (Throwable e) { // must catch Throwable
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    private File downloadFileInternal(String url, File targetFile) throws IOException {
        HttpURLConnection connection = mClient.open(new URL(url));

        Closer closer = Closer.create();
        try {
            // Read the response.
            InputStream in = closer.register(connection.getInputStream());
            Files.asByteSink(targetFile).writeFrom(in);
            return targetFile;
        } catch (Throwable e) { // must catch Throwable
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }
}