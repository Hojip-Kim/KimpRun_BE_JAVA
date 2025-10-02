package kimp.cdn.component.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CfCreateAccessRuleReqeustDto {
    @JsonProperty("mode")
    private String mode;
    @JsonProperty("configuration")
    private CfConfiguration configurations;
    @JsonProperty("notes")
    private String notes;
}
