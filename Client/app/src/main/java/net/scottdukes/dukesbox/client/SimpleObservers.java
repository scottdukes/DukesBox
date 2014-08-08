package net.scottdukes.dukesbox.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observer;

public final class SimpleObservers {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleObservers.class);

    public static final Observer<Object> objectObserver = new Observer<Object>() {
        @Override
        public void onCompleted() {
            LOGGER.info("Complete");
        }

        @Override
        public void onError(Throwable e) {
            LOGGER.error("Error", e);
        }

        @Override
        public void onNext(Object args) {
            LOGGER.info("Got {}", args);
        }
    };

    public static final Observer<String> stringObserver = new Observer<String>() {
        @Override
        public void onCompleted() {
            LOGGER.info("Complete");
        }

        @Override
        public void onError(Throwable e) {
            LOGGER.error("Error", e);
        }

        @Override
        public void onNext(String args) {
            LOGGER.info("Got {}", args);
        }
    };
}
