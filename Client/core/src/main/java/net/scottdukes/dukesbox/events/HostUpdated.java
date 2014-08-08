package net.scottdukes.dukesbox.events;

public class HostUpdated {
    private final String mHostAddress;

    public HostUpdated(String hostAddress) {
        mHostAddress = hostAddress;
    }

    public String getHostAddress() {
        return mHostAddress;
    }
}
