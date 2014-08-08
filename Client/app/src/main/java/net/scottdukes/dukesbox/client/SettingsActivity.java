package net.scottdukes.dukesbox.client;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.scottdukes.dukesbox.presenters.SettingsPresenter;
import net.scottdukes.dukesbox.views.ISettingsView;

import javax.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class SettingsActivity extends RoboActivity implements ISettingsView {
    @InjectView(R.id.hostAddress)
    private TextView mHostAddressText;

    @InjectView(R.id.hostPort)
    private TextView mHostPortText;

    @Inject
    private SettingsPresenter mPresenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        mPresenter.init(this);
    }

    public void cancelClicked(View view) {
        mPresenter.cancelClicked();
    }

    public void okClicked(View view) {
        mPresenter.saveClicked();
    }

    @Override
    public String getAddressText() {
        return mHostAddressText.getText().toString();
    }

    @Override
    public void setAddressText(String address) {
        mHostAddressText.setText(address);
    }

    @Override
    public String getPortText() {
        return mHostPortText.getText().toString();
    }

    @Override
    public void setPortText(String port) {
        mHostPortText.setText(port);
    }

    @Override
    public void exit() {
        this.finish();
        mPresenter.dispose();
    }

    @Override
    public void onBackPressed() {
        mPresenter.cancelClicked();
    }
}