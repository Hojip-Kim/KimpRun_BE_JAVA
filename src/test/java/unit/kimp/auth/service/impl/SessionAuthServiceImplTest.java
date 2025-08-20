package unit.kimp.auth.service.impl;

import kimp.auth.dto.LoginMemberResponseDto;
import kimp.auth.service.serviceImpl.SessionAuthServiceImpl;
import kimp.member.util.NicknameGeneratorUtils;
import kimp.security.user.CustomUserDetails;
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

    private SessionAuthServiceImpl sessionAuthService;

    @BeforeEach
    void setUp() {
        sessionAuthService = new SessionAuthServiceImpl(nicknameGeneratorUtils);
    }

    @Test
    @DisplayName("인증 상태 확인: 올바른 인증 상태와 사용자 세부 정보 반환")
    void shouldReturnCorrectAuthenticationStatusAndUserDetails() {
        // Arrange
        String email = "test@example.com";
        String username = "testUser";
        UserRole role = UserRole.USER;

        when(customUserDetails.getEmail()).thenReturn(email);
        when(customUserDetails.getUsername()).thenReturn(username);
        when(customUserDetails.getRole()).thenReturn(role);

        // Act
        LoginMemberResponseDto result = sessionAuthService.checkAuthStatus(customUserDetails);

        // Assert
        assertTrue(result.isAuthenticated());
        assertNotNull(result.getMember());
        assertEquals(email, result.getMember().getEmail());
        assertEquals(username, result.getMember().getName());
        assertEquals(role.name(), result.getMember().getRole());
    }

    @Test
    @DisplayName("인증 상태 확인: null 이메일 처리")
    void shouldHandleNullEmailInCheckAuthStatus() {
        // Arrange
        String username = "testUser";
        UserRole role = UserRole.USER;

        when(customUserDetails.getEmail()).thenReturn(null);
        when(customUserDetails.getUsername()).thenReturn(username);
        when(customUserDetails.getRole()).thenReturn(role);

        // Act
        LoginMemberResponseDto result = sessionAuthService.checkAuthStatus(customUserDetails);

        // Assert
        assertTrue(result.isAuthenticated());
        assertNotNull(result.getMember());
        assertEquals("", result.getMember().getEmail());
        assertEquals(username, result.getMember().getName());
        assertEquals(role.name(), result.getMember().getRole());
    }
}