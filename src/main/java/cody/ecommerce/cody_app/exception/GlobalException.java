package cody.ecommerce.cody_app.exception;

import lombok.Getter;
import lombok.Setter;
import cody.ecommerce.cody_app.dto.Error;

public class GlobalException extends RuntimeException {
    private final Error<?> error;

    /**
     * Instantiates a new Resource not found exception.
     *
     * @param message the message
     */
    public GlobalException(String message) {
        super(message);
        this.error = null;
    }

    /**
     * Instantiates a new Data existed exception.
     *
     * @param message the message
     * @param error   the error
     */
    public GlobalException(String message, Error<?> error) {
        super(message);
        this.error = error;
    }

    public Error<?> getError() {
        return error;
    }

    public static GlobalException error(String message, Error<?> error) {
        return new GlobalException(message, error);
    }

    public static GlobalException error(Error<?> error) {
        return new GlobalException(error.getDetail(), error);
    }
}