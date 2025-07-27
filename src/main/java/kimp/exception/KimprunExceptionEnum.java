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

    // Custom Exception
    UPBIT_API_CALL_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "E0005"),
    
    // Validation Exceptions
    INVALID_PARAMETER_EXCEPTION(HttpStatus.BAD_REQUEST, "E0006"),
    INVALID_PAGE_PARAMETER_EXCEPTION(HttpStatus.BAD_REQUEST, "E0007"),
    INVALID_ID_PARAMETER_EXCEPTION(HttpStatus.BAD_REQUEST, "E0008"),
    
    // Resource Exceptions
    RESOURCE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "E0009"),
    RESOURCE_ALREADY_EXISTS_EXCEPTION(HttpStatus.CONFLICT, "E0010"),
    
    // Authentication Exceptions
    AUTHENTICATION_REQUIRED_EXCEPTION(HttpStatus.UNAUTHORIZED, "E0011"),
    
    // Business Logic Exceptions
    EXCHANGE_SCRAPING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "E0012"),
    CMC_API_RATE_LIMIT_EXCEPTION(HttpStatus.TOO_MANY_REQUESTS, "E0013"),
    WEBSOCKET_SESSION_EXCEPTION(HttpStatus.BAD_REQUEST, "E0014"),
    
    // Data Processing Exceptions
    DATA_PROCESSING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "E0015"),
    PYTHON_SERVICE_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "E0016"),
    ;

    private final HttpStatus httpStatus;
    private final String errorCode;

    KimprunExceptionEnum(HttpStatus httpStatus, String errorCode) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }




}