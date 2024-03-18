package io.openfuture.openmessanger.exception;

public class FirstTimeLoginException extends ServiceException {

    public FirstTimeLoginException() {
        super();
    }

    public FirstTimeLoginException(String message) {
        super(message);
    }

    public FirstTimeLoginException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
