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

import butterknife.ButterKnife;
import butterknife.InjectView;
import roboguice.activity.RoboActivity;

public class MainActivity extends RoboActivity implements IMainView {
    private final Logger logger = LoggerFactory.getLogger(MainActivity.class);

    @InjectView(R.id.syncStatusText)
    TextView syncStatusText;

    @InjectView(R.id.hostText)
    TextView hostText;

    @Inject
    private MainPresenter presenter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.inject(this);
        presenter.init(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logger.debug("onDestroy");
        presenter.dispose();
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
                presenter.updateConfigClicked();
                return true;

            case R.id.quitButton:
                presenter.quitClicked();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setHostText(String value) {
        hostText.setText(value);
    }

    @Override
    public void setStatusText(String value) {
        syncStatusText.setText(value);
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
        presenter.startClicked();
    }

    public void stopClicked(View view) {
        presenter.stopClicked();
    }
}