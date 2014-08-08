package net.scottdukes.dukesbox;

public interface IDukesBoxConfig {
    String getBaseUrl();

    int getPollPeriodInSeconds();

    String getAddress();

    void setAddress(String value);

    int getPort();

    void setPort(int value);
}
