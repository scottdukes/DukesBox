package net.scottdukes.dukesbox.client;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.scottdukes.dukesbox.presenters.SettingsPresenter;
import net.scottdukes.dukesbox.views.ISettingsView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import roboguice.activity.RoboActivity;

public class SettingsActivity extends RoboActivity implements ISettingsView {
    @InjectView(R.id.hostAddress)
    TextView hostAddressText;

    @InjectView(R.id.hostPort)
    TextView hostPortText;

    @Inject
    private SettingsPresenter presenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.inject(this);
        presenter.init(this);
    }

    public void cancelClicked(View view) {
        presenter.cancelClicked();
    }

    public void okClicked(View view) {
        presenter.saveClicked();
    }

    @Override
    public String getAddressText() {
        return hostAddressText.getText().toString();
    }

    @Override
    public void setAddressText(String address) {
        hostAddressText.setText(address);
    }

    @Override
    public String getPortText() {
        return hostPortText.getText().toString();
    }

    @Override
    public void setPortText(String port) {
        hostPortText.setText(port);
    }

    @Override
    public void exit() {
        this.finish();
        presenter.dispose();
    }

    @Override
    public void onBackPressed() {
        presenter.cancelClicked();
    }
}