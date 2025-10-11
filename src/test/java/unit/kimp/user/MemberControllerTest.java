package unit.kimp.user;

import kimp.exception.KimprunException;
import kimp.exception.response.ApiResponse;
import kimp.security.user.CustomUserDetails;
import kimp.user.controller.MemberController;
import kimp.user.dto.response.UserDto;
import kimp.user.dto.response.UserWithIdNameEmailDto;
import kimp.user.dto.request.*;
import kimp.user.dto.response.AdminResponse;
import kimp.user.dto.response.EmailVerifyCodeResponseDTO;
import kimp.user.dto.response.EmailVerifyResponseDTO;
import kimp.user.entity.Member;
import kimp.user.enums.UserRole;
import kimp.user.entity.MemberRole;
import kimp.user.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

    @InjectMocks
    private MemberController memberController;

    @Mock
    private MemberService memberService;

    @Mock
    private CustomUserDetails customUserDetails;

    private Member mockMember;
    private UserDto mockUserDto;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MemberRole userRole = new MemberRole("user-role-key", UserRole.USER);
        mockMember = new Member("test@example.com", "testuser", "password", userRole);
        // Use reflection to set the id field as it's not exposed by a setter
        Field idField = Member.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(mockMember, 1L);
        mockUserDto = new UserDto("test@example.com", "testuser", UserRole.USER);

        // Set adminUrl field using reflection for the admin redirect test
        Field adminUrlField = MemberController.class.getDeclaredField("adminUrl");
        adminUrlField.setAccessible(true);
        adminUrlField.set(memberController, "http://admin.test.com");

        lenient().when(customUserDetails.getId()).thenReturn(1L);
    }

    @Test
    @DisplayName("현재 사용자 정보 조회")
    void shouldReturnCurrentUserInformation() {
        // Arrange
        when(memberService.getmemberById(any())).thenReturn(mockUserDto);

        // Act
        ApiResponse<UserDto> response = memberController.getMember(customUserDetails);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockUserDto, response.getData());
        verify(memberService, times(1)).getmemberById(any());
    }

    @Test
    @DisplayName("ID로 사용자 정보 조회 (관리자 전용)")
    void shouldReturnMemberByIdForManager() throws IOException {
        // Arrange
        when(memberService.getmemberById(any())).thenReturn(mockUserDto);

        // Act
        ApiResponse<UserDto> response = memberController.findMemberById(customUserDetails, 1L);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockUserDto, response.getData());
        verify(memberService, times(1)).getmemberById(any());
    }

    @Test
    @DisplayName("이메일 인증 코드 확인 성공")
    void shouldVerifyEmailCodeSuccessfully() {
        // Arrange
        EmailVerifyCodeRequestDTO requestDTO = new EmailVerifyCodeRequestDTO("test@example.com", "123456");
        when(memberService.verifyCode(any())).thenReturn(true);

        // Act
        ApiResponse<EmailVerifyCodeResponseDTO> response = memberController.verifyEmailCode(requestDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData().getIsVerified());
        verify(memberService, times(1)).verifyCode(any());
    }

    @Test
    @DisplayName("이메일 인증 코드 확인 실패")
    void shouldFailEmailCodeVerification() {
        // Arrange
        EmailVerifyCodeRequestDTO requestDTO = new EmailVerifyCodeRequestDTO("test@example.com", "wrongcode");
        when(memberService.verifyCode(any())).thenReturn(false);

        // Act
        ApiResponse<EmailVerifyCodeResponseDTO> response = memberController.verifyEmailCode(requestDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertFalse(response.getData().getIsVerified());
        verify(memberService, times(1)).verifyCode(any());
    }

    @Test
    @DisplayName("이메일 인증 코드 전송: 기존 사용자")
    void shouldSendEmailVerificationCodeWhenUserExists() {
        // Arrange
        EmailVerifyRequestDTO requestDTO = new EmailVerifyRequestDTO("test@example.com");
        when(memberService.getmemberByEmail(anyString())).thenReturn(mockMember);
        when(memberService.sendEmailVerifyCode(any())).thenReturn("123456");

        // Act
        ApiResponse<EmailVerifyResponseDTO> response = memberController.sendEmailVerificationCode(requestDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData().getIsExisted());
        assertEquals("123456", response.getData().getVerificationCode());
        verify(memberService, times(1)).getmemberByEmail("test@example.com");
        verify(memberService, times(1)).sendEmailVerifyCode(any());
    }

    @Test
    @DisplayName("이메일 인증 코드 전송: 신규 사용자")
    void shouldSendEmailVerificationCodeWhenUserDoesNotExist() {
        // Arrange
        EmailVerifyRequestDTO requestDTO = new EmailVerifyRequestDTO("new@example.com");
        when(memberService.getmemberByEmail(anyString())).thenReturn(null);

        // Act
        ApiResponse<EmailVerifyResponseDTO> response = memberController.sendEmailVerificationCode(requestDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertFalse(response.getData().getIsExisted());
        assertNull(response.getData().getVerificationCode());
        verify(memberService, times(1)).getmemberByEmail("new@example.com");
        verify(memberService, never()).sendEmailVerifyCode(any());
    }

    @Test
    @DisplayName("새로운 사용자 생성")
    void shouldCreateNewMember() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO("newuser@example.com", "newuser", "password123");
        MemberRole userRole = new MemberRole("user-role-key", UserRole.USER);
        Member newMember = new Member("newuser@example.com", "newuser", "password123", userRole);
        UserDto newUserDto = new UserDto("newuser@example.com", "newuser", UserRole.USER);

        when(memberService.createMember(any())).thenReturn(newUserDto);

        // Act
        ApiResponse<UserDto> response = memberController.createMember(createUserDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(newUserDto, response.getData());
        verify(memberService, times(1)).createMember(any());
    }

    @Test
    @DisplayName("사용자 역할 업데이트 (관리자 전용)")
    void shouldUpdateUserRoleForManager() {
        // Arrange
        UpdateUserRoleDTO updateUserRoleDTO = new UpdateUserRoleDTO(1L, UserRole.MANAGER);
        MemberRole managerRole = new MemberRole("manager-role-key", UserRole.MANAGER);
        Member updatedMember = new Member("test@example.com", "testuser", "password", managerRole);
        UserDto updatedUserDto = new UserDto("test@example.com", "testuser", UserRole.MANAGER);

        when(memberService.grantRole(any())).thenReturn(updatedUserDto);

        // Act
        ApiResponse<UserDto> response = memberController.updateUserRole(customUserDetails, updateUserRoleDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(updatedUserDto, response.getData());
        verify(memberService, times(1)).grantRole(any());
    }

    @Test
    @DisplayName("사용자 정보 업데이트")
    void shouldUpdateMemberInformation() {
        // Arrange
        UpdateUserPasswordDTO updatePasswordDTO = new UpdateUserPasswordDTO("oldpass", "newpass");
        MemberRole userRole = new MemberRole("user-role-key", UserRole.USER);
        Member updatedMember = new Member("test@example.com", "testuser", "newpass", userRole);
        UserDto updatedUserDto = new UserDto("test@example.com", "testuser", UserRole.USER);

        when(memberService.updateMember(any())).thenReturn(updatedUserDto);

        // Act
        ApiResponse<UserDto> response = memberController.updateMember(customUserDetails, updatePasswordDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(updatedUserDto, response.getData());
        verify(memberService, times(1)).updateMember(any());
    }

    @Test
    @DisplayName("사용자 정보 업데이트 실패: DTO null")
    void shouldThrowExceptionWhenUpdateMemberWithNullDto() {
        // Arrange
        UpdateUserPasswordDTO updatePasswordDTO = null;

        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> memberController.updateMember(customUserDetails, updatePasswordDTO));
        assertEquals("UpdateUserPasswordDTO cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("사용자 닉네임 업데이트")
    void shouldUpdateMemberNickname() {
        // Arrange
        UpdateUserNicknameDTO updateNicknameDTO = new UpdateUserNicknameDTO("newnickname");
        MemberRole userRole = new MemberRole("user-role-key", UserRole.USER);
        Member updatedMember = new Member("test@example.com", "newnickname", "password", userRole);
        UserWithIdNameEmailDto updatedUserDto = new UserWithIdNameEmailDto("test@example.com", "newnickname", UserRole.USER.name(), 1L);

        when(memberService.updateNickname(any())).thenReturn(updatedUserDto);

        // Act
        ApiResponse<UserWithIdNameEmailDto> response = memberController.updateMemberNickname(customUserDetails, updateNicknameDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(updatedUserDto.getName(), response.getData().getName());
        verify(memberService, times(1)).updateNickname(any());
    }

    @Test
    @DisplayName("사용자 닉네임 업데이트 실패: DTO null")
    void shouldThrowExceptionWhenUpdateMemberNicknameWithNullDto() {
        // Arrange
        UpdateUserNicknameDTO updateNicknameDTO = null;

        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> memberController.updateMemberNickname(customUserDetails, updateNicknameDTO));
        assertEquals("UpdateUserNicknameDTO cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("사용자 비활성화 (소프트 삭제)")
    void shouldDeactivateMemberSuccessfully() {
        // Arrange
        DeActivateUserDTO deActivateUserDTO = new DeActivateUserDTO("password");
        when(memberService.deActivateMember(any())).thenReturn(true);

        // Act
        ApiResponse<Boolean> response = memberController.deActivateMember(customUserDetails, deActivateUserDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(memberService, times(1)).deActivateMember(any());
    }

    @Test
    @DisplayName("사용자 비활성화 실패: DTO null")
    void shouldThrowExceptionWhenDeactivateMemberWithNullDto() {
        // Arrange
        DeActivateUserDTO deActivateUserDTO = null;

        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> memberController.deActivateMember(customUserDetails, deActivateUserDTO));
        assertEquals("DeActivateUserDTO cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("사용자 삭제 (관리자/운영자 전용)")
    void shouldDeleteMemberSuccessfully() {
        // Arrange
        DeleteUserDTO deleteUserDTO = new DeleteUserDTO(1L);
        when(memberService.deleteMember(any())).thenReturn(true);

        // Act
        ApiResponse<Boolean> response = memberController.deleteMember(customUserDetails, deleteUserDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(memberService, times(1)).deleteMember(any());
    }

    @Test
    @DisplayName("사용자 삭제 실패: DTO null")
    void shouldThrowExceptionWhenDeleteMemberWithNullDto() {
        // Arrange
        DeleteUserDTO deleteUserDTO = null;

        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> memberController.deleteMember(customUserDetails, deleteUserDTO));
        assertEquals("DeleteUserDTO cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("관리자 페이지 리다이렉트 (운영자 전용)")
    void shouldRedirectToAdminPage() throws IOException {
        // Arrange
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        // Act
        ApiResponse<AdminResponse> response = memberController.redirectAdmin(customUserDetails, httpServletResponse);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData().getResponse());
    }
}
