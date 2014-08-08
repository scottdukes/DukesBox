package net.scottdukes.dukesbox.api;

public interface ResponseHandler<T> {
    void handle(T t);
}
