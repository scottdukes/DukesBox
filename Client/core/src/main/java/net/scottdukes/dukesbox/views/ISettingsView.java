package net.scottdukes.dukesbox.views;

public interface ISettingsView {
    String getAddressText();

    void setAddressText(String address);

    String getPortText();

    void setPortText(String port);

    void exit();
}
