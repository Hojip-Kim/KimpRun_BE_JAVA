package kimp.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.user.entity.Declaration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class DeclarationResponse {
    @JsonProperty
    private String fromMember;
    @JsonProperty
    private String toMember;
    @JsonProperty
    private String reason;
    @JsonProperty
    private LocalDateTime createdAt;
    @JsonProperty
    private LocalDateTime updatedAt;

    public static DeclarationResponse from(Declaration declaration) {
        return DeclarationResponse.builder()
                .fromMember(declaration.getFromMember())
                .toMember(declaration.getToMember())
                .reason(declaration.getReason())
                .createdAt(declaration.getRegistedAt())
                .updatedAt(declaration.getUpdatedAt())
                .build();
    }
}
