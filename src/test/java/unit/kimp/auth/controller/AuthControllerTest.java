package unit.kimp.auth.controller;

import kimp.auth.controller.AuthController;
import kimp.auth.dto.CheckAuthResponseDto;
import kimp.auth.service.AuthService;
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
        UserWithIdNameEmailDto userWithIdNameEmailDto = new UserWithIdNameEmailDto("test@example.com", "testUser", "USER");
        CheckAuthResponseDto mockResponseDto = new CheckAuthResponseDto(true, userWithIdNameEmailDto);
        when(authService.checkAuthStatus(authenticatedUser)).thenReturn(mockResponseDto);

        // Act
        ApiResponse<CheckAuthResponseDto> apiResponse = authController.checkMemberStatus(authenticatedUser, request, response);

        // Assert
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertEquals(200, apiResponse.getStatus());
        assertEquals(mockResponseDto, apiResponse.getData());
        verify(authService, times(1)).checkAuthStatus(authenticatedUser);
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("인증되지 않은 사용자 상태 확인")
    void shouldReturnUnauthorizedForUnauthenticatedUser() {
        // Arrange
        UserDetails unauthenticatedUser = null;

        // Act
        ApiResponse<CheckAuthResponseDto> apiResponse = authController.checkMemberStatus(unauthenticatedUser, request, response);

        // Assert
        assertNotNull(apiResponse);
        assertFalse(apiResponse.isSuccess());
        assertEquals(401, apiResponse.getStatus());
        assertEquals("UNAUTHORIZED", apiResponse.getMessage());
        assertEquals("Authentication required", apiResponse.getDetail());
        verify(authService, never()).checkAuthStatus(any(CustomUserDetails.class));
        assertEquals(401, response.getStatus());
    }
}
