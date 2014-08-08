package net.scottdukes.dukesbox.presenters;

import com.google.common.eventbus.EventBus;

import net.scottdukes.dukesbox.IDukesBoxConfig;
import net.scottdukes.dukesbox.events.HostUpdated;
import net.scottdukes.dukesbox.views.ISettingsView;

import javax.inject.Inject;

public class SettingsPresenter {
    private final IDukesBoxConfig mConfig;
    private ISettingsView mView;

    @Inject
    private EventBus mEventBus;

    @Inject
    public SettingsPresenter(IDukesBoxConfig config) {
        mConfig = config;
    }

    public void init(ISettingsView view) {
        mView = view;
        view.setAddressText(mConfig.getAddress());
        view.setPortText(String.valueOf(mConfig.getPort()));
    }

    public void saveClicked() {
        try {
            String address = mView.getAddressText();
            int port = Integer.parseInt(mView.getPortText());
            mConfig.setAddress(address);
            mConfig.setPort(port);
            mEventBus.post(new HostUpdated(mConfig.getBaseUrl()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        mView.exit();
    }

    public void cancelClicked() {
        mView.exit();
    }

    public void dispose() {

    }
}
