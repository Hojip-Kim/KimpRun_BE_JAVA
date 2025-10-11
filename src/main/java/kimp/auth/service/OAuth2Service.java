package kimp.auth.service;

import kimp.auth.dto.internal.OauthProcessDTO;
import kimp.user.dto.internal.UserCopyDto;

public interface OAuth2Service {

    public UserCopyDto processOAuth2member(OauthProcessDTO oauth2member);

}
