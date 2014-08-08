package net.scottdukes.dukesbox.client;

import android.content.SharedPreferences;

import net.scottdukes.dukesbox.IDukesBoxConfig;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class DukesBoxConfig implements IDukesBoxConfig {
    public static final int DEFAULT_PORT = 3010;
    public static final String DEFAULT_ADDRESS = "10.1.20.170";
    public static final String PREFERENCE_ADDRESS = "HostAddress";
    public static final String PREFERENCE_PORT = "HostPort";

    @Inject
    Provider<SharedPreferences> mSharedPreferencesProvider;
    private int mPollPeriodInSeconds = 5;

    @Override
    public String getBaseUrl() {
        return String.format("http://%s:%d/", getAddress(), getPort());
    }

    @Override
    public int getPollPeriodInSeconds() {
        return mPollPeriodInSeconds;
    }

    @Override
    public String getAddress() {
        return sharedPreferences().getString(PREFERENCE_ADDRESS, DEFAULT_ADDRESS);
    }

    @Override
    public void setAddress(String value) {
        sharedPreferences().edit().putString(PREFERENCE_ADDRESS, value).apply();
    }

    @Override
    public int getPort() {
        return sharedPreferences().getInt(PREFERENCE_PORT, DEFAULT_PORT);
    }

    @Override
    public void setPort(int value) {
        sharedPreferences().edit().putInt(PREFERENCE_PORT, value).apply();
    }

    public void setPollPeriodInSeconds(int pollPeriodInSeconds) {
        mPollPeriodInSeconds = pollPeriodInSeconds;
    }

    private SharedPreferences sharedPreferences() {
        return mSharedPreferencesProvider.get();
    }
}
