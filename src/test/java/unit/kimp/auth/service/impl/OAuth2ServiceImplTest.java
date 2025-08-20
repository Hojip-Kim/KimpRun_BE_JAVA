package unit.kimp.auth.service.impl;

import kimp.auth.dto.OauthProcessDTO;
import kimp.auth.service.serviceImpl.OAuth2ServiceImpl;
import kimp.user.dto.UserCopyDto;
import kimp.user.dto.request.CreateUserDTO;
import kimp.user.entity.Member;
import kimp.user.enums.Oauth;
import kimp.user.enums.UserRole;
import kimp.user.entity.MemberRole;
import kimp.user.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OAuth2ServiceImplTest {

    @Mock
    private MemberService memberService;

    @Mock
    private OAuth2User oauth2User;

    private OAuth2ServiceImpl oauth2Service;

    @BeforeEach
    void setUp() {
        oauth2Service = new OAuth2ServiceImpl(memberService);
    }

    @Test
    @DisplayName("OAuth2 신규 사용자 처리")
    void shouldCreateNewMemberWhenOAuth2MemberDoesNotExist() {
        // Arrange
        String email = "test@example.com";
        String name = "Test User";
        String providerId = "123456789";
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("name", name);
        attributes.put("sub", providerId);

        when(oauth2User.getAttributes()).thenReturn(attributes);
        when(memberService.getmemberByEmail(email)).thenReturn(null);
        when(memberService.getMemberByOAuthProviderId(anyString(), anyString())).thenReturn(null);

        MemberRole userRole = new MemberRole("user-role-key", UserRole.USER);
        Member newMember = new Member(email, name, "generated-password", userRole);

        when(memberService.createMember(any(CreateUserDTO.class))).thenReturn(newMember);

        OauthProcessDTO oauthProcessDTO = new OauthProcessDTO(accessToken, refreshToken, "Bearer", 3600L, "email profile", oauth2User);

        // Act
        UserCopyDto result = oauth2Service.processOAuth2member(oauthProcessDTO);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(name, result.getNickname());
        assertEquals(UserRole.USER, result.getRole());

        ArgumentCaptor<CreateUserDTO> createUserDTOCaptor = ArgumentCaptor.forClass(CreateUserDTO.class);
        verify(memberService).createMember(createUserDTOCaptor.capture());

        CreateUserDTO capturedDTO = createUserDTOCaptor.getValue();
        assertEquals(email, capturedDTO.getEmail());
        assertEquals(name, capturedDTO.getNickname());
        assertEquals(Oauth.GOOGLE, capturedDTO.getOauth());
        assertEquals(providerId, capturedDTO.getProviderId());
        assertEquals(accessToken, capturedDTO.getAccessToken());
    }

    @Test
    @DisplayName("OAuth2 기존 사용자 처리")
    void shouldReturnExistingMemberWhenOAuth2MemberExists() {
        // Arrange
        String email = "existing@example.com";
        String name = "Existing User";
        String providerId = "987654321";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("name", name);
        attributes.put("sub", providerId);

        when(oauth2User.getAttributes()).thenReturn(attributes);

        MemberRole userRole = new MemberRole("user-role-key", UserRole.USER);
        Member existingMember = new Member(email, name, "existing-password", userRole);

        when(memberService.getmemberByEmail(email)).thenReturn(existingMember);
        when(memberService.getMemberByOAuthProviderId(anyString(), anyString())).thenReturn(null);
        when(memberService.attachOAuthToMember(any(Member.class), anyString(), anyString(), anyString(), anyString(), anyString(), any(), anyString())).thenReturn(existingMember);

        OauthProcessDTO oauthProcessDTO = new OauthProcessDTO("access-token", "refresh-token", "Bearer", 3600L, "email profile", oauth2User);

        // Act
        UserCopyDto result = oauth2Service.processOAuth2member(oauthProcessDTO);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(name, result.getNickname());
        assertEquals(UserRole.USER, result.getRole());

        verify(memberService, never()).createMember(any(CreateUserDTO.class));
        verify(memberService).attachOAuthToMember(any(Member.class), anyString(), anyString(), anyString(), anyString(), anyString(), any(), anyString());
    }
}
