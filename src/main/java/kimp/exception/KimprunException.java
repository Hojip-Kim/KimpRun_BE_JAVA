package kimp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KimprunException extends RuntimeException {
    private KimprunExceptionEnum exceptionEnum;
    private final HttpStatus httpStatus;
    private final String trace;

    public KimprunException(KimprunExceptionEnum exceptionEnum, String message, HttpStatus httpStatus, String trace) {
        super(message);
        this.exceptionEnum = exceptionEnum;
        this.httpStatus = httpStatus;
        this.trace = trace;
    }




}
