package kimp.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdateAnonNicknameResponse {
    private String email;
    private String name;
    private String role;
    private Long number;

    public UpdateAnonNicknameResponse(String email, String name, String role, Long number) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.number = number;
    }
}
