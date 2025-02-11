package kimp.exception;

import kimp.exception.response.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class KimprunExceptionAdvice{

    //HttpStatus httpStatus, String message, String customStatus, String trace
    @ExceptionHandler(KimprunException.class)
    public ResponseEntity<ErrorResponseDTO> handleKimprunException(KimprunException e) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(e.getHttpStatus(), e.getMessage(), e.getTrace());
        return new ResponseEntity<>(errorResponseDTO, e.getHttpStatus());
    }



}
