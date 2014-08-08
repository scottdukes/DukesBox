package net.scottdukes.dukesbox.views;

public interface IMainView {
    void setHostText(String value);

    void setStatusText(String value);

    void exit();

    void navigateToSettings();

    IProgressView addProgress(String name, int max);

    void removeProgress(String name);
}
