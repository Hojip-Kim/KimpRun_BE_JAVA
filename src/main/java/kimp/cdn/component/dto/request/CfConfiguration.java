package kimp.cdn.component.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CfConfiguration {
    @JsonProperty("target")
    private String target;
    @JsonProperty("value")
    private String value;

    public CfConfiguration(String target, String value) {
        this.target = target;
        this.value = value;
    }
}
