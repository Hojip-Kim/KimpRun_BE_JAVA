package kimp.cdn.component.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CfAccessRuleResult {
    // rule id
    @JsonProperty("id")
    private String id;

    public CfAccessRuleResult(String id) {
        this.id = id;
    }
}
