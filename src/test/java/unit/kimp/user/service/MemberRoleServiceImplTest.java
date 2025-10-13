package unit.kimp.user.service;

import kimp.user.dao.MemberRoleDao;
import kimp.user.entity.MemberRole;
import kimp.user.enums.UserRole;
import kimp.user.service.member.impl.MemberRoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberRoleServiceImplTest {

    @Mock
    private MemberRoleDao memberRoleDao;

    @InjectMocks
    private MemberRoleServiceImpl memberRoleService;

    private MemberRole memberRole;

    @BeforeEach
    void setUp() {
        memberRole = new MemberRole("test-role-key", UserRole.USER);
    }

    @Test
    @DisplayName("역할 생성 성공")
    void shouldCreateRole() {
        when(memberRoleDao.existsByRoleKey(anyString())).thenReturn(false);
        when(memberRoleDao.save(any(MemberRole.class))).thenReturn(memberRole);

        MemberRole result = memberRoleService.createRole("test-role-key", UserRole.USER);

        assertNotNull(result);
        assertEquals("test-role-key", result.getRoleKey());
        assertEquals(UserRole.USER, result.getRoleName());
        verify(memberRoleDao, times(1)).save(any(MemberRole.class));
    }

    @Test
    @DisplayName("기본 사용자 역할 조회")
    void shouldGetDefaultUserRole() {
        when(memberRoleDao.findByRoleName(UserRole.USER)).thenReturn(Optional.of(memberRole));

        MemberRole result = memberRoleService.getDefaultUserRole();

        assertNotNull(result);
        assertEquals(UserRole.USER, result.getRoleName());
        verify(memberRoleDao, times(1)).findByRoleName(UserRole.USER);
    }

    @Test
    @DisplayName("기본 사용자 역할 생성 (존재하지 않을 경우)")
    void shouldCreateDefaultUserRoleIfNotExists() {
        when(memberRoleDao.findByRoleName(UserRole.USER)).thenReturn(Optional.empty());
        when(memberRoleDao.save(any(MemberRole.class))).thenReturn(memberRole);

        MemberRole result = memberRoleService.getDefaultUserRole();

        assertNotNull(result);
        assertEquals(UserRole.USER, result.getRoleName());
        verify(memberRoleDao, times(1)).save(any(MemberRole.class));
    }

    @Test
    @DisplayName("역할 키로 조회")
    void shouldGetRoleByKey() {
        when(memberRoleDao.findByRoleKey("test-role-key")).thenReturn(Optional.of(memberRole));

        MemberRole result = memberRoleService.getRoleByKey("test-role-key");

        assertNotNull(result);
        assertEquals("test-role-key", result.getRoleKey());
        verify(memberRoleDao, times(1)).findByRoleKey("test-role-key");
    }
}