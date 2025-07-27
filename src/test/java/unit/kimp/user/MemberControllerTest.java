package unit.kimp.user;

import kimp.exception.KimprunException;
import kimp.exception.response.ApiResponse;
import kimp.security.user.CustomUserDetails;
import kimp.user.MemberController;
import kimp.user.dto.UserDto;
import kimp.user.dto.UserWithIdNameEmailDto;
import kimp.user.dto.request.*;
import kimp.user.dto.response.AdminResponse;
import kimp.user.dto.response.EmailVerifyCodeResponseDTO;
import kimp.user.dto.response.EmailVerifyResponseDTO;
import kimp.user.entity.Member;
import kimp.user.enums.UserRole;
import kimp.user.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
        mockMember = new Member("test@example.com", "testuser", "password");
        // Use reflection to set the id field as it's not exposed by a setter
        Field idField = Member.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(mockMember, 1L);
        mockMember.grantRole(UserRole.USER); // Use the public method to set role
        mockUserDto = new UserDto("test@example.com", "testuser", UserRole.USER);

        when(customUserDetails.getId()).thenReturn(1L);
    }

    @Test
    @DisplayName("현재 사용자 정보 조회")
    void shouldReturnCurrentUserInformation() {
        // Arrange
        when(memberService.getmemberById(anyLong())).thenReturn(mockMember);
        when(memberService.convertUserToUserDto(any(Member.class))).thenReturn(mockUserDto);

        // Act
        ApiResponse<UserDto> response = memberController.getmember(customUserDetails);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockUserDto, response.getData());
        verify(memberService, times(1)).getmemberById(1L);
        verify(memberService, times(1)).convertUserToUserDto(mockMember);
    }

    @Test
    @DisplayName("ID로 사용자 정보 조회 (관리자 전용)")
    void shouldReturnMemberByIdForManager() throws IOException {
        // Arrange
        when(memberService.getmemberById(anyLong())).thenReturn(mockMember);
        when(memberService.convertUserToUserDto(any(Member.class))).thenReturn(mockUserDto);

        // Act
        ApiResponse<UserDto> response = memberController.findMemberById(customUserDetails, 1L);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(mockUserDto, response.getData());
        verify(memberService, times(1)).getmemberById(1L);
        verify(memberService, times(1)).convertUserToUserDto(mockMember);
    }

    @Test
    @DisplayName("이메일 인증 코드 확인 성공")
    void shouldVerifyEmailCodeSuccessfully() {
        // Arrange
        EmailVerifyCodeRequestDTO requestDTO = new EmailVerifyCodeRequestDTO("test@example.com", "123456");
        when(memberService.verifyCode(anyString(), anyString())).thenReturn(true);

        // Act
        ApiResponse<EmailVerifyCodeResponseDTO> response = memberController.verifyEmailCode(requestDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData().getIsVerified());
        verify(memberService, times(1)).verifyCode("test@example.com", "123456");
    }

    @Test
    @DisplayName("이메일 인증 코드 확인 실패")
    void shouldFailEmailCodeVerification() {
        // Arrange
        EmailVerifyCodeRequestDTO requestDTO = new EmailVerifyCodeRequestDTO("test@example.com", "wrongcode");
        when(memberService.verifyCode(anyString(), anyString())).thenReturn(false);

        // Act
        ApiResponse<EmailVerifyCodeResponseDTO> response = memberController.verifyEmailCode(requestDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertFalse(response.getData().getIsVerified());
        verify(memberService, times(1)).verifyCode("test@example.com", "wrongcode");
    }

    @Test
    @DisplayName("이메일 인증 코드 전송: 기존 사용자")
    void shouldSendEmailVerificationCodeWhenUserExists() {
        // Arrange
        EmailVerifyRequestDTO requestDTO = new EmailVerifyRequestDTO("test@example.com");
        when(memberService.getmemberByEmail(anyString())).thenReturn(mockMember);

        // Act
        ApiResponse<EmailVerifyResponseDTO> response = memberController.sendEmailVerificationCode(requestDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData().getIsExisted());
        assertNull(response.getData().getVerificationCode());
        verify(memberService, times(1)).getmemberByEmail("test@example.com");
        verify(memberService, never()).sendEmailVerifyCode(anyString());
    }

    @Test
    @DisplayName("이메일 인증 코드 전송: 신규 사용자")
    void shouldSendEmailVerificationCodeWhenUserDoesNotExist() {
        // Arrange
        EmailVerifyRequestDTO requestDTO = new EmailVerifyRequestDTO("new@example.com");
        when(memberService.getmemberByEmail(anyString())).thenReturn(null);
        when(memberService.sendEmailVerifyCode(anyString())).thenReturn("654321");

        // Act
        ApiResponse<EmailVerifyResponseDTO> response = memberController.sendEmailVerificationCode(requestDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertFalse(response.getData().getIsExisted());
        assertEquals("654321", response.getData().getVerificationCode());
        verify(memberService, times(1)).getmemberByEmail("new@example.com");
        verify(memberService, times(1)).sendEmailVerifyCode("new@example.com");
    }

    @Test
    @DisplayName("새로운 사용자 생성")
    void shouldCreateNewMember() {
        // Arrange
        CreateUserDTO createUserDTO = new CreateUserDTO("newuser@example.com", "newuser", "password123");
        Member newMember = new Member("newuser@example.com", "newuser", "password123");
        UserDto newUserDto = new UserDto("newuser@example.com", "newuser", UserRole.USER);

        when(memberService.createMember(any(CreateUserDTO.class))).thenReturn(newMember);
        when(memberService.convertUserToUserDto(any(Member.class))).thenReturn(newUserDto);

        // Act
        ApiResponse<UserDto> response = memberController.createMember(createUserDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(newUserDto, response.getData());
        verify(memberService, times(1)).createMember(createUserDTO);
        verify(memberService, times(1)).convertUserToUserDto(newMember);
    }

    @Test
    @DisplayName("사용자 역할 업데이트 (관리자 전용)")
    void shouldUpdateUserRoleForManager() {
        // Arrange
        UpdateUserRoleDTO updateUserRoleDTO = new UpdateUserRoleDTO(1L, UserRole.MANAGER);
        Member updatedMember = new Member("test@example.com", "testuser", "password");
        UserDto updatedUserDto = new UserDto("test@example.com", "testuser", UserRole.MANAGER);

        when(memberService.grantRole(anyLong(), any(UserRole.class))).thenReturn(updatedMember);
        when(memberService.convertUserToUserDto(any(Member.class))).thenReturn(updatedUserDto);

        // Act
        ApiResponse<UserDto> response = memberController.updateUserRole(customUserDetails, updateUserRoleDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(updatedUserDto, response.getData());
        verify(memberService, times(1)).grantRole(1L, UserRole.MANAGER);
        verify(memberService, times(1)).convertUserToUserDto(updatedMember);
    }

    @Test
    @DisplayName("사용자 정보 업데이트")
    void shouldUpdateMemberInformation() {
        // Arrange
        UpdateUserPasswordDTO updatePasswordDTO = new UpdateUserPasswordDTO("oldpass", "newpass");
        Member updatedMember = new Member("test@example.com", "testuser", "newpass");
        UserDto updatedUserDto = new UserDto("test@example.com", "testuser", UserRole.USER);

        when(memberService.updateMember(anyLong(), any(UpdateUserPasswordDTO.class))).thenReturn(updatedMember);
        when(memberService.convertUserToUserDto(any(Member.class))).thenReturn(updatedUserDto);

        // Act
        ApiResponse<UserDto> response = memberController.updateMember(customUserDetails, updatePasswordDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(updatedUserDto, response.getData());
        verify(memberService, times(1)).updateMember(1L, updatePasswordDTO);
        verify(memberService, times(1)).convertUserToUserDto(updatedMember);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 실패: DTO null")
    void shouldThrowExceptionWhenUpdateMemberWithNullDto() {
        // Arrange
        UpdateUserPasswordDTO updatePasswordDTO = null;

        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> memberController.updateMember(customUserDetails, updatePasswordDTO));
        assertEquals("UpdateUserPasswordDTO cannot be null", exception.getTrace());
    }

    @Test
    @DisplayName("사용자 닉네임 업데이트")
    void shouldUpdateMemberNickname() {
        // Arrange
        UpdateUserNicknameDTO updateNicknameDTO = new UpdateUserNicknameDTO("newnickname");
        Member updatedMember = new Member("test@example.com", "newnickname", "password");
        // Member entity doesn't have public setters
        mockMember.grantRole(UserRole.USER);
        UserWithIdNameEmailDto updatedUserDto = new UserWithIdNameEmailDto("test@example.com", "newnickname", UserRole.USER.name());

        when(memberService.updateNickname(anyLong(), any(UpdateUserNicknameDTO.class))).thenReturn(updatedMember);

        // Act
        ApiResponse<UserWithIdNameEmailDto> response = memberController.updateMemberNickname(customUserDetails, updateNicknameDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertEquals(updatedUserDto.getName(), response.getData().getName());
        verify(memberService, times(1)).updateNickname(1L, updateNicknameDTO);
    }

    @Test
    @DisplayName("사용자 닉네임 업데이트 실패: DTO null")
    void shouldThrowExceptionWhenUpdateMemberNicknameWithNullDto() {
        // Arrange
        UpdateUserNicknameDTO updateNicknameDTO = null;

        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> memberController.updateMemberNickname(customUserDetails, updateNicknameDTO));
        assertEquals("UpdateUserNicknameDTO cannot be null", exception.getTrace());
    }

    @Test
    @DisplayName("사용자 비활성화 (소프트 삭제)")
    void shouldDeactivateMemberSuccessfully() {
        // Arrange
        DeActivateUserDTO deActivateUserDTO = new DeActivateUserDTO("password");
        when(memberService.deActivateMember(anyLong(), any(DeActivateUserDTO.class))).thenReturn(true);

        // Act
        ApiResponse<Boolean> response = memberController.deActivateMember(customUserDetails, deActivateUserDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(memberService, times(1)).deActivateMember(1L, deActivateUserDTO);
    }

    @Test
    @DisplayName("사용자 비활성화 실패: DTO null")
    void shouldThrowExceptionWhenDeactivateMemberWithNullDto() {
        // Arrange
        DeActivateUserDTO deActivateUserDTO = null;

        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> memberController.deActivateMember(customUserDetails, deActivateUserDTO));
        assertEquals("DeActivateUserDTO cannot be null", exception.getTrace());
    }

    @Test
    @DisplayName("사용자 삭제 (관리자/운영자 전용)")
    void shouldDeleteMemberSuccessfully() {
        // Arrange
        DeleteUserDTO deleteUserDTO = new DeleteUserDTO(1L);
        when(memberService.deleteMember(any(DeleteUserDTO.class))).thenReturn(true);

        // Act
        ApiResponse<Boolean> response = memberController.deleteMember(customUserDetails, deleteUserDTO);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertTrue(response.getData());
        verify(memberService, times(1)).deleteMember(deleteUserDTO);
    }

    @Test
    @DisplayName("사용자 삭제 실패: DTO null")
    void shouldThrowExceptionWhenDeleteMemberWithNullDto() {
        // Arrange
        DeleteUserDTO deleteUserDTO = null;

        // Act & Assert
        KimprunException exception = assertThrows(KimprunException.class, () -> memberController.deleteMember(customUserDetails, deleteUserDTO));
        assertEquals("DeleteUserDTO cannot be null", exception.getTrace());
    }

    @Test
    @DisplayName("관리자 페이지 리다이렉트 (운영자 전용)")
    void shouldRedirectToAdminPage() throws IOException {
        // Arrange
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        when(memberService.getmemberById(anyLong())).thenReturn(mockMember);

        // Act
        ApiResponse<AdminResponse> response = memberController.redirectAdmin(customUserDetails, httpServletResponse);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData().getResponse());
        verify(memberService, times(1)).getmemberById(1L);
    }
}
