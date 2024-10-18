package kimp.user.dto.request;

import kimp.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateUserRoleDTO {

    private long userId;
    private UserRole role;

}
