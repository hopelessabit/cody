package cody.ecommerce.cody_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import cody.ecommerce.cody_app.dto.Error;

/**
 * The type Internal Server Exception exception.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InternalServerExceptionException extends GlobalException {
    public InternalServerExceptionException(String message) {
        super(message);
    }

    public InternalServerExceptionException(String message, Error<?> error) {
        super(message, error);
    }
}
