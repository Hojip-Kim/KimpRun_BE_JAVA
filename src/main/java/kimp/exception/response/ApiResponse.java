package kimp.exception.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private final int status;
    private final String error;
    private final T data;
    private final String trace;

    private ApiResponse(int status, String error, T data, String trace) {
        this.status = status;
        this.error = error;
        this.data = data;
        this.trace = trace;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), null, data, null);
    }

    public static <T> ApiResponse<T> success(HttpStatus status, T data) {
        return new ApiResponse<>(status.value(), null, data, null);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String errorType, String trace) {
        return new ApiResponse<>(status.value(), errorType, null, trace);
    }

    public static <T> ApiResponse<T> error(int status, String errorType, String trace) {
        return new ApiResponse<>(status, errorType, null, trace);
    }
}