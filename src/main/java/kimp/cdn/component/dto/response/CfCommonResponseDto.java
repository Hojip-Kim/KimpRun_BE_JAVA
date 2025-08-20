package kimp.cdn.component.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CfCommonResponseDto<T> {
    @JsonProperty("success")
    private boolean success;
    private T result;

    public CfCommonResponseDto(boolean success, T result) {
        this.success = success;
        this.result = result;
    }
}
