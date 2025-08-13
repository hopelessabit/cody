package cody.ecommerce.cody_app.util;

import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.exception.*;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import  cody.ecommerce.cody_app.dto.ResponseData;

import java.util.function.Supplier;


public class ResponseUtil {
    public final static String SQL_ERROR_MESSAGE = "Lỗi database";
    public static <T> ResponseEntity<ResponseData<T>> getResponse(T response, String message){
        return new ResponseEntity<>(ResponseData.ok(response, message), HttpStatus.OK);
    }

    public static <T> ResponseEntity<ResponseData<T>> getResponse(Supplier<T> responseSupplier, String message) {
        T response = responseSupplier.get(); // Call the function
        return new ResponseEntity<>(ResponseData.ok(response, message), HttpStatus.OK);
    }

    public static  <T> ResponseEntity<ResponseData<T>> getResponse(ResponseData<T> response) {
        if (response.getStatus() == HttpStatus.OK.value())
            return ResponseEntity.ok(response);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }
}
