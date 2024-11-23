package kimp.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeActivateUserDTO {

    private String password;

    public DeActivateUserDTO(String password) {
        if(password.isEmpty()){
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.password = password;
    }
}
