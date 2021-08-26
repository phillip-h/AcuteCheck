package com.github.phillip.h.acutecheck.step;

public class StepException extends RuntimeException {

    public StepException() {
    }

    public StepException(String message) {
        super(message);
    }

    public StepException(String message, Throwable cause) {
        super(message, cause);
    }

    public StepException(Throwable cause) {
        super(cause);
    }

    public StepException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
