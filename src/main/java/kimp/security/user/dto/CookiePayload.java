package kimp.security.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CookiePayload {
    public String id;
    public long iat;

    public CookiePayload(String id, long iat) {
        this.id = id;
        this.iat = iat;
    }
}
