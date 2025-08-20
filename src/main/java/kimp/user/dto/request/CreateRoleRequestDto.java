package kimp.user.dto.request;

import kimp.user.enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateRoleRequestDto {
    
    private String roleKey;
    private UserRole roleName;
    
    public CreateRoleRequestDto(String roleKey, UserRole roleName) {
        this.roleKey = roleKey;
        this.roleName = roleName;
    }
}