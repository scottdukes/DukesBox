package net.scottdukes.dukesbox.client;

import android.app.Application;
import android.os.Environment;

import net.scottdukes.dukesbox.AppPaths;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.io.File;

@Singleton
public class AppPathsImpl implements AppPaths {
    private final Application mApplication;

    @Inject
    public AppPathsImpl(Application application) {
        mApplication = application;
    }

    @Override
    public File getCacheDir() {
        return mApplication.getCacheDir();
    }

    @Override
    public File getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory();
    }
}
