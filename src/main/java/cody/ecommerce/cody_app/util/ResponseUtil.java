package cody.ecommerce.cody_app.util;

import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import  cody.ecommerce.cody_app.dto.ResponseData;

import java.util.function.Supplier;


public class ResponseUtil {
    public final static String SQL_ERROR_MESSAGE = "Lỗi database";
//    public static <T> ResponseEntity<ResponseData<T>> getResponse(T response, String message){
//        return new ResponseEntity<>(ResponseData.ok(response, message), HttpStatus.OK);
//    }

    public static <T> ResponseEntity<ResponseData<T>> getResponse(Supplier<T> responseSupplier, String message) {
        T response;
        try {
            response = responseSupplier.get(); // Call the function
        } catch (NotFoundException e) {
            return new ResponseEntity<>(ResponseData.error("Không tìm thấy", e.getError(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(ResponseData.error("Yêu cầu không hợp lệ", e.getError(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } catch (DataExistedException e) {
            return new ResponseEntity<>(ResponseData.error("Dữ liệu đã tồn tại", e.getError(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } catch (UnauthorizeException e) {
            return new ResponseEntity<>(ResponseData.error("", e.getError(), HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(ResponseData.error("Lỗi máy chủ nội bộ", e.getError(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (GlobalException e) {
            return new ResponseEntity<>(ResponseData.error("Lỗi không xác định", e.getError(), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e){
            return new ResponseEntity<>(ResponseData.error("Lỗi không xác định", Error.build(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ResponseData.ok(response, message), HttpStatus.OK);
    }

    public static  <T> ResponseEntity<ResponseData<T>> getResponse(ResponseData<T> response) {
        if (response.getStatus() == HttpStatus.OK.value())
            return ResponseEntity.ok(response);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }
}
