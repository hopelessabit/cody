package cody.ecommerce.cody_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import cody.ecommerce.cody_app.dto.Error;

import java.util.List;

/**
 * The type Data existed exception.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends GlobalException {

    private List<String> ids;
    /**
     * Instantiates a new Resource not found exception.
     *
     * @param message the message
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Data existed exception.
     *
     * @param message the message
     * @param error  the error
     */
    public NotFoundException(String message, Error<?> error) {
        super(message, error);
    }
}
