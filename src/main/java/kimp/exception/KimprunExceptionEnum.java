package kimp.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum KimprunExceptionEnum {

    // System Exception
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, "E0001"),
    ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED, "E0002"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E0003"),
    REQUEST_ACCEPTED(HttpStatus.ACCEPTED, "E0004"),

    // Cusotm Exception
    UPBIT_API_CALL_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "E0005"),
    ;

    // Custom Exception


    KimprunExceptionEnum(HttpStatus httpStatus, String errorCode) {
    }




}