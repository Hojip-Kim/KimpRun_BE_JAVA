package unit.kimp.auth.service.impl;

import kimp.auth.dto.response.LoginMemberResponseDto;
import kimp.auth.service.serviceImpl.SessionAuthServiceImpl;
import kimp.auth.vo.CheckAuthStatusVo;
import kimp.user.service.MemberService;
import kimp.user.util.NicknameGeneratorUtils;
import kimp.security.user.CustomUserDetails;
import kimp.user.entity.Member;
import kimp.user.entity.MemberRole;
import kimp.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SessionAuthServiceImplTest {

    @Mock
    private NicknameGeneratorUtils nicknameGeneratorUtils;

    @Mock
    private CustomUserDetails customUserDetails;

    @Mock
    private MemberService memberService;

    private SessionAuthServiceImpl sessionAuthService;

    @BeforeEach
    void setUp() {
        sessionAuthService = new SessionAuthServiceImpl(nicknameGeneratorUtils, memberService);
    }

    @Test
    @DisplayName("인증 상태 확인: 올바른 인증 상태와 사용자 세부 정보 반환")
    void shouldReturnCorrectAuthenticationStatusAndUserDetails() {
        // Arrange
        String email = "test@example.com";
        String username = "testUser";
        UserRole role = UserRole.USER;
        Long memberId = 1L;
        
        MemberRole memberRole = new MemberRole("user-role-key", role);
        Member mockMember = new Member(email, username, "password", memberRole);
        // Set id using reflection since it's not exposed by setter
        try {
            java.lang.reflect.Field idField = Member.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockMember, memberId);
        } catch (Exception e) {
            fail("Failed to set member id");
        }

        when(memberService.getMemberEntityById(memberId)).thenReturn(mockMember);

        // Act
        CheckAuthStatusVo vo = new CheckAuthStatusVo(memberId);
        LoginMemberResponseDto result = sessionAuthService.checkAuthStatus(vo);

        // Assert
        assertTrue(result.isAuthenticated());
        assertNotNull(result.getMember());
        assertEquals(email, result.getMember().getEmail());
        assertEquals(username, result.getMember().getName());
        assertEquals(role.getName(), result.getMember().getRole());
    }

    @Test
    @DisplayName("인증 상태 확인: null VO 처리")
    void shouldHandleNullVoInCheckAuthStatus() {
        // Act
        LoginMemberResponseDto result = sessionAuthService.checkAuthStatus(null);

        // Assert
        assertNull(result);
    }
    
    @Test
    @DisplayName("인증 상태 확인: null memberId 처리")
    void shouldHandleNullMemberIdInCheckAuthStatus() {
        // Act
        CheckAuthStatusVo vo = new CheckAuthStatusVo(null);
        LoginMemberResponseDto result = sessionAuthService.checkAuthStatus(vo);

        // Assert
        assertNull(result);
    }
}