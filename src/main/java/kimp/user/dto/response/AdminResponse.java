package kimp.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminResponse {
    private String response;

    public AdminResponse(String response) {
        this.response = response;
    }
}
