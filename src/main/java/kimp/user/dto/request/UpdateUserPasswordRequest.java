package kimp.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdateUserPasswordRequest {
    private String email;
    private String password;

    public UpdateUserPasswordRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
