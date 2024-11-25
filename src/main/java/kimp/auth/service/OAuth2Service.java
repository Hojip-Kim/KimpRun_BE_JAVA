package kimp.auth.service;

import kimp.auth.dto.OauthProcessDTO;
import kimp.user.dto.UserCopyDto;

public interface OAuth2Service {

    public UserCopyDto processOAuth2member(OauthProcessDTO oauth2member);

}
