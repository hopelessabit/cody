package cody.ecommerce.cody_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import cody.ecommerce.cody_app.dto.Error;

/**
 * The type Internal Server Error exception.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InternalServerErrorException extends GlobalException {
    public InternalServerErrorException(String message) {
        super(message);
    }

    public InternalServerErrorException(String message, Error<?> error) {
        super(message, error);
    }
}
