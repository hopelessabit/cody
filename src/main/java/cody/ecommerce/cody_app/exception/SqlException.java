package cody.ecommerce.cody_app.exception;

import cody.ecommerce.cody_app.dto.Error;

public class SqlException extends GlobalException {
    public SqlException(String message) {
        super(message);
    }
    public SqlException(String message, Error<?> error) {
      super(message, error);
    }
}
