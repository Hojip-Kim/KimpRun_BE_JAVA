package kimp.user.dto.response;

import kimp.user.entity.MemberRole;
import kimp.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRoleResponseDto {

    private Long id;
    private String roleKey;
    private UserRole roleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MemberRoleResponseDto(MemberRole memberRole) {
        this.id = memberRole.getId();
        this.roleKey = memberRole.getRoleKey();
        this.roleName = memberRole.getRoleName();
        this.createdAt = memberRole.getRegistedAt();
        this.updatedAt = memberRole.getUpdatedAt();
    }
}