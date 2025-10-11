package kimp.user.controller;

import kimp.exception.response.ApiResponse;
import kimp.user.dto.request.CreateActivityRankRequestDto;
import kimp.user.service.AdminService;
import kimp.user.vo.CreateActivityRankVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Hojip-Kim
 * @version 1.0
 *
 * 'OPERATOR' 권한을 가진 admin 유저만 사용할 수 있는 엔드포인트.
 */

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('OPERATOR')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // activityRank create
    @PostMapping("/activityRank")
    public ApiResponse<Void> createActivityRank(@RequestBody CreateActivityRankRequestDto requestBody) {
        CreateActivityRankVo vo = new CreateActivityRankVo(requestBody.getGrade());
        adminService.createActivityRank(vo);
        return ApiResponse.success(null);
    }

}
