package cody.ecommerce.cody_app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * The type Response data.
 *
 * @param <T> the type parameter
 */
@Data
public class ResponseData<T> {
    private final int status;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Error<?> error;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Error<?> getError() {
        return error;
    }

    /**
     * Instantiates a new Response data.
     *
     * @param status  the status
     * @param message the message
     */
// PUT, PATCH, DELETE
    public ResponseData(int status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * Instantiates a new Response data.
     *
     * @param status  the status
     * @param message the message
     * @param error   the error
     */
    // GET, POST
    public ResponseData(int status, String message, Error error) {
        this.status = status;
        this.message = message;
        this.error = error;
    }

    /**
     * Instantiates a new Response data.
     *
     * @param status  the status
     * @param message the message
     * @param data    the data
     */
    // GET, POST
    public ResponseData(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseData<T> ok(T data, String message) {
        return new ResponseData<>(HttpStatus.OK.value(), message, data);
    }

    public static <T> ResponseData<T> ok(String message) {
        return new ResponseData<>(HttpStatus.OK.value(), message);
    }

    public static <T> ResponseData<T> error(String message, Error<?> error, HttpStatus status) {
        return new ResponseData<>(status.value(), message, error);
    }
}
