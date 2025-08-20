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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MemberRoleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MemberRoleService memberRoleService;

    @InjectMocks
    private MemberRoleController memberRoleController;

    private ObjectMapper objectMapper;
    private MemberRole memberRole;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberRoleController).build();
        objectMapper = new ObjectMapper();
        memberRole = new MemberRole("test-role-key", UserRole.USER);
    }

    @Test
    @DisplayName("역할 생성")
    void shouldCreateRole() throws Exception {
        CreateRoleRequestDto request = new CreateRoleRequestDto("test-role-key", UserRole.USER);
        when(memberRoleService.createRole(anyString(), any(UserRole.class))).thenReturn(memberRole);

        mockMvc.perform(post("/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roleKey").value("test-role-key"))
                .andExpect(jsonPath("$.data.roleName").value("USER"));
    }

    @Test
    @DisplayName("역할 조회")
    void shouldGetRoleById() throws Exception {
        when(memberRoleService.getRoleById(1L)).thenReturn(memberRole);

        mockMvc.perform(get("/role/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roleKey").value("test-role-key"));
    }

    @Test
    @DisplayName("모든 역할 조회")
    void shouldGetAllRoles() throws Exception {
        List<MemberRole> roles = Arrays.asList(memberRole);
        when(memberRoleService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get("/role"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].roleKey").value("test-role-key"));
    }
}