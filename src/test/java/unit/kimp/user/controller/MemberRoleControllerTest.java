package unit.kimp.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.user.controller.MemberRoleController;
import kimp.user.dto.request.CreateRoleRequestDto;
import kimp.user.entity.MemberRole;
import kimp.user.enums.UserRole;
import kimp.user.service.MemberRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberRoleController.class)
public class MemberRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberRoleService memberRoleService;

    @Autowired
    private ObjectMapper objectMapper;

    private MemberRole memberRole;

    @BeforeEach
    void setUp() {
        memberRole = new MemberRole("test-role-key", UserRole.USER);
    }

    @Test
    @DisplayName("역할 생성 - OPERATOR 권한")
    @WithMockUser(authorities = "OPERATOR")
    void shouldCreateRoleWithOperatorAuthority() throws Exception {
        CreateRoleRequestDto request = new CreateRoleRequestDto("test-role-key", UserRole.USER);
        when(memberRoleService.createRole(anyString(), any(UserRole.class))).thenReturn(memberRole);

        mockMvc.perform(post("/role")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roleKey").value("test-role-key"))
                .andExpect(jsonPath("$.data.roleName").value("USER"));
    }

    @Test
    @DisplayName("역할 조회 - MANAGER 권한")
    @WithMockUser(authorities = "MANAGER")
    void shouldGetRoleByIdWithManagerAuthority() throws Exception {
        when(memberRoleService.getRoleById(1L)).thenReturn(memberRole);

        mockMvc.perform(get("/role/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roleKey").value("test-role-key"));
    }

    @Test
    @DisplayName("모든 역할 조회 - MANAGER 권한")
    @WithMockUser(authorities = "MANAGER")
    void shouldGetAllRolesWithManagerAuthority() throws Exception {
        List<MemberRole> roles = Arrays.asList(memberRole);
        when(memberRoleService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get("/role"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].roleKey").value("test-role-key"));
    }

    @Test
    @DisplayName("권한 없는 사용자 접근 거부")
    @WithMockUser(authorities = "USER")
    void shouldDenyAccessForUnauthorizedUser() throws Exception {
        CreateRoleRequestDto request = new CreateRoleRequestDto("test-role-key", UserRole.USER);

        mockMvc.perform(post("/role")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}