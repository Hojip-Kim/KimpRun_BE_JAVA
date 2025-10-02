package kimp.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWithIdNameEmailDto {
    private String email;
    private String name;
    private String role;
    private Long memberId;
}
