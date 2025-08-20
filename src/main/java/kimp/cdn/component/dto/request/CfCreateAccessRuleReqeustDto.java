package kimp.cdn.component.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CfCreateAccessRuleReqeustDto {
    @JsonProperty("mode")
    private String mode;
    @JsonProperty("configuration")
    private CfConfiguration configurations;
    @JsonProperty("notes")
    private String notes;

    public CfCreateAccessRuleReqeustDto(String mode, CfConfiguration configurations, String notes) {
        this.mode = mode;
        this.configurations = configurations;
        this.notes = notes;
    }

}
