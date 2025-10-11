package kimp.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class UpdateAnonNicknameResponse {
    private String email;
    private String name;
    private String role;
    private Long number;
}
