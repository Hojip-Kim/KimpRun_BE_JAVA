package kimp.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.user.entity.Declaration;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
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

    public DeclarationResponse(String fromMember, String toMember, String reason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.fromMember = fromMember;
        this.toMember = toMember;
        this.reason = reason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static DeclarationResponse from(Declaration declaration) {
        return new DeclarationResponse(
                declaration.getFromMember(),
                declaration.getToMember(),
                declaration.getReason(),
                declaration.getRegistedAt(),
                declaration.getUpdatedAt()
        );
    }
}
