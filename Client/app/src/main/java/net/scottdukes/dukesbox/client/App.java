package net.scottdukes.dukesbox.client;

import android.app.Application;

import com.google.inject.Module;

import roboguice.RoboGuice;
import roboguice.config.DefaultRoboModule;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerModule(new AppModule());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void registerModule(Module module) {
        DefaultRoboModule defaultRoboModule = createDefaultRoboModule(this);
        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE, defaultRoboModule, module);
    }

    private DefaultRoboModule createDefaultRoboModule(Application application) {
        return RoboGuice.newDefaultRoboModule(application);
    }
}
