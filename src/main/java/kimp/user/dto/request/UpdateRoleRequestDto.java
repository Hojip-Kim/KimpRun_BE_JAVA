package kimp.user.dto.request;

import kimp.user.enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateRoleRequestDto {
    
    private UserRole roleName;
    
    public UpdateRoleRequestDto(UserRole roleName) {
        this.roleName = roleName;
    }
}