package kimp.security.cookie;

import kimp.security.user.dto.CookiePayload;

public interface CookieVerifier {

    public CookiePayload verify(String cookieValue, String secret);

    public String createSignedCookie(String id, String secret);

}
