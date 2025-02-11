package kimp.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailVerifyRequestDTO {

    private String email;

    public EmailVerifyRequestDTO(String email) {
        this.email = email;
    }
}
