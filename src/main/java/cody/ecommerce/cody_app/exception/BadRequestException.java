package cody.ecommerce.cody_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import cody.ecommerce.cody_app.dto.Error;

/**
 * The type Data existed exception.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends GlobalException {

  /**
   * Instantiates a new Resource not found exception.
   *
   * @param message the message
   */
  public BadRequestException(String message) {
    super(message);
  }

  /**
   * Instantiates a new Data existed exception.
   *
   * @param message the message
   * @param error  the error
   */
  public BadRequestException(String message, Error<?> error) {
    super(message, error);
  }
}
