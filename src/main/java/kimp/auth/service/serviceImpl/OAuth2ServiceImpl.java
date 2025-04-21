package kimp.auth.service.serviceImpl;

import kimp.auth.dto.OauthProcessDTO;
import kimp.auth.service.OAuth2Service;
import kimp.user.dto.UserCopyDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.entity.Member;
import kimp.user.enums.Oauth;
import kimp.user.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class OAuth2ServiceImpl implements OAuth2Service {

    private final MemberService memberService;

    public OAuth2ServiceImpl(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    @Transactional
    public UserCopyDto processOAuth2member(OauthProcessDTO oauthProcessDTO) {
        Map<String, Object> attributes = oauthProcessDTO.getOauth2User().getAttributes();

        String email = (String) attributes.get("email");
        String nickname = (String) attributes.get("name");
        String providerId = (String) attributes.get("sub");

        log.info("OAuth2 attributes: {}", attributes);

        String password = UUID.randomUUID().toString();

        Member member = memberService.getmemberByEmail(email);

        // member가 없을경우 회원가입 진행
        if(member == null){
            CreateUserDTO createUserDTO = new CreateUserDTO(nickname, email, password);

            // 현재 google oauth만 제공하므로 google로 설정
            createUserDTO.setOauth(Oauth.GOOGLE);
            createUserDTO.setProviderId(providerId);
            createUserDTO.setAccessToken(oauthProcessDTO.getAccessToken());
            if(createUserDTO.getRefreshToken() != null){createUserDTO.setRefreshToken(oauthProcessDTO.getRefreshToken());}


            member = memberService.createMember(createUserDTO);
        }

        return new UserCopyDto(member.getId(), member.getEmail(), member.getPassword(), member.getNickname(), member.getRole());
    }

}
