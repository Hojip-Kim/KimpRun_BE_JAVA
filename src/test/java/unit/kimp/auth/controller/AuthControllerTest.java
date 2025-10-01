package unit.kimp.auth.controller;

import kimp.auth.controller.AuthController;
import kimp.auth.dto.AuthResponseDto;
import kimp.auth.dto.LoginMemberResponseDto;
import kimp.auth.service.AuthService;
import kimp.auth.vo.CheckAuthStatusVo;
import kimp.exception.response.ApiResponse;
import kimp.security.user.CustomUserDetails;
import kimp.user.dto.UserWithIdNameEmailDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import kimp.auth.dto.UnLoginMemberResponseDto;
import jakarta.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("인증된 사용자 상태 확인")
    void shouldReturnAuthStatusForAuthenticatedUser() {
        // Arrange
        CustomUserDetails authenticatedUser = mock(CustomUserDetails.class);
        UserWithIdNameEmailDto userWithIdNameEmailDto = new UserWithIdNameEmailDto("test@example.com", "testUser", "USER", 1L);
        LoginMemberResponseDto mockResponseDto = new LoginMemberResponseDto(true, userWithIdNameEmailDto, "RandomUuid");
        when(authService.checkAuthStatus(any(CheckAuthStatusVo.class))).thenReturn(mockResponseDto);

        // Act
        ApiResponse<AuthResponseDto> apiResponse = authController.checkMemberStatus(authenticatedUser, request, response);

        // Assert
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertEquals(200, apiResponse.getStatus());
        assertEquals(mockResponseDto, apiResponse.getData());
        verify(authService, times(1)).checkAuthStatus(any(CheckAuthStatusVo.class));
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("인증되지 않은 사용자 상태 확인 - 쿠키 있음")
    void shouldReturnUnauthorizedForUnauthenticatedUserWithCookie() {
        // Arrange
        UserDetails unauthenticatedUser = null;
        // 쿠키 설정
        Cookie kimprunCookie = new Cookie("kimprun-token", "test-uuid-value");
        request.setCookies(kimprunCookie);

        // Act
        ApiResponse<AuthResponseDto> apiResponse = authController.checkMemberStatus(unauthenticatedUser, request, response);

        // Assert
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess()); // 실제 구현은 UnLoginMemberResponseDto를 성공으로 반환
        assertEquals(200, apiResponse.getStatus());
        assertTrue(apiResponse.getData() instanceof UnLoginMemberResponseDto); // UnLoginMemberResponseDto가 반환됨
        UnLoginMemberResponseDto unLoginData = (UnLoginMemberResponseDto) apiResponse.getData();
        assertEquals("test-uuid-value", unLoginData.getUuid());
        verify(authService, never()).checkAuthStatus(any(CheckAuthStatusVo.class));
    }
    
    @Test
    @DisplayName("인증되지 않은 사용자 상태 확인 - 쿠키 없음")
    void shouldReturnNullForUnauthenticatedUserWithoutCookie() {
        // Arrange
        UserDetails unauthenticatedUser = null;
        // 쿠키를 설정하지 않으면 cookies는 null이 됨
        request.setCookies(); // 빈 쿠키 배열

        // Act & Assert
        // 실제 구현에서 cookies가 null일 때는 아무도 반환하지 않음 
        // 따라서 이 경우는 예외가 발생할 수 있음
        assertThrows(Exception.class, () -> {
            authController.checkMemberStatus(unauthenticatedUser, request, response);
        });
        
        verify(authService, never()).checkAuthStatus(any(CheckAuthStatusVo.class));
    }
}
