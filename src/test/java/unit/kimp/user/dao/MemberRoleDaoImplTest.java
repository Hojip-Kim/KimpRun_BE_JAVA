package unit.kimp.user.dao;

import kimp.user.dao.impl.MemberRoleDaoImpl;
import kimp.user.entity.MemberRole;
import kimp.user.enums.UserRole;
import kimp.user.repository.user.MemberRoleRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberRoleDaoImplTest {

    @Mock
    private MemberRoleRepository memberRoleRepository;

    @InjectMocks
    private MemberRoleDaoImpl memberRoleDao;

    private MemberRole memberRole;

    @BeforeEach
    void setUp() {
        memberRole = new MemberRole("test-role-key", UserRole.USER);
    }

    @Test
    @DisplayName("역할 저장 성공")
    void shouldSaveRole() {
        when(memberRoleRepository.save(any(MemberRole.class))).thenReturn(memberRole);

        MemberRole result = memberRoleDao.save(memberRole);

        assertNotNull(result);
        assertEquals("test-role-key", result.getRoleKey());
        assertEquals(UserRole.USER, result.getRoleName());
        verify(memberRoleRepository, times(1)).save(memberRole);
    }

    @Test
    @DisplayName("역할 키로 조회 성공")
    void shouldFindByRoleKey() {
        when(memberRoleRepository.findByRoleKey("test-role-key")).thenReturn(Optional.of(memberRole));

        Optional<MemberRole> result = memberRoleDao.findByRoleKey("test-role-key");

        assertTrue(result.isPresent());
        assertEquals("test-role-key", result.get().getRoleKey());
        verify(memberRoleRepository, times(1)).findByRoleKey("test-role-key");
    }

    @Test
    @DisplayName("역할 이름으로 조회 성공")
    void shouldFindByRoleName() {
        when(memberRoleRepository.findByRoleName(UserRole.USER)).thenReturn(Optional.of(memberRole));

        Optional<MemberRole> result = memberRoleDao.findByRoleName(UserRole.USER);

        assertTrue(result.isPresent());
        assertEquals(UserRole.USER, result.get().getRoleName());
        verify(memberRoleRepository, times(1)).findByRoleName(UserRole.USER);
    }

    @Test
    @DisplayName("역할 키 존재 확인")
    void shouldCheckIfRoleKeyExists() {
        when(memberRoleRepository.existsByRoleKey("test-role-key")).thenReturn(true);

        boolean result = memberRoleDao.existsByRoleKey("test-role-key");

        assertTrue(result);
        verify(memberRoleRepository, times(1)).existsByRoleKey("test-role-key");
    }
}