package kimp.exception;

import kimp.exception.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class KimprunExceptionAdvice{

    @ExceptionHandler(KimprunException.class)
    public ResponseEntity<ApiResponse<Object>> handleKimprunException(KimprunException e) {
        log.error("KimprunException occurred: {}", e.getMessage(), e);
        ApiResponse<Object> response = ApiResponse.error(e.getHttpStatus().value(), e.getHttpStatus().name(), e.getTrace());
        return new ResponseEntity<>(response, e.getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException occurred: {}", e.getMessage(), e);
        ApiResponse<Object> response = ApiResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception e) {
        log.error("Unexpected exception occurred: {}", e.getMessage(), e);
        ApiResponse<Object> response = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
