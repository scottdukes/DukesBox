package net.scottdukes.dukesbox.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.scottdukes.dukesbox.presenters.MainPresenter;
import net.scottdukes.dukesbox.views.IMainView;
import net.scottdukes.dukesbox.views.IProgressView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class MainActivity extends RoboActivity implements IMainView {
    private final Logger mLogger = LoggerFactory.getLogger(MainActivity.class);

    @InjectView(R.id.syncStatusText)
    private TextView mSyncStatusText;

    @InjectView(R.id.hostText)
    private TextView mHostText;

    @Inject
    private MainPresenter mPresenter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mPresenter.init(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLogger.debug("onDestroy");
        mPresenter.dispose();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.options, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updateConfigButton:
                mPresenter.updateConfigClicked();
                return true;

            case R.id.quitButton:
                mPresenter.quitClicked();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setHostText(String value) {
        mHostText.setText(value);
    }

    @Override
    public void setStatusText(String value) {
        mSyncStatusText.setText(value);
    }

    @Override
    public void exit() {
        this.finish();
    }

    @Override
    public void navigateToSettings() {
        final Context context = this;

        Intent intent = new Intent();
        intent.setClass(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    public IProgressView addProgress(String name, int max) {
//    final ProgressBar progressBar = new ProgressBar(this);
//    progressBar.setMax(max);

        return new IProgressView() {
            @Override
            public void setProgress(int value) {
                //progressBar.setProgress(value);
            }
        };
    }

    @Override
    public void removeProgress(String name) {
        // TODO
    }

    public void startClicked(View view) {
        mPresenter.startClicked();
    }

    public void stopClicked(View view) {
        mPresenter.stopClicked();
    }
}