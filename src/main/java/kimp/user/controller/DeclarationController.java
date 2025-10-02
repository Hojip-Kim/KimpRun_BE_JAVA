package kimp.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import kimp.common.dto.PageRequestDto;
import kimp.exception.response.ApiResponse;
import kimp.user.dto.request.DeclarationMemberRequest;
import kimp.user.dto.response.DeclarationResponse;
import kimp.user.service.DeclarationService;
import kimp.user.vo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/declaration")
public class DeclarationController {
    private final DeclarationService declarationService;

    public DeclarationController(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }


    @PreAuthorize("hasAuthority('OPERATOR')")
    @GetMapping
    public ApiResponse<Page<DeclarationResponse>> getDeclarationResponsePage(@ModelAttribute PageRequestDto pageRequestDto) {
        Pageable pageable = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize());
        GetDeclarationsVo vo = new GetDeclarationsVo(pageable);
        Page<DeclarationResponse> declarations = declarationService.getDeclarations(vo);

        return ApiResponse.success(declarations);
    }

    @PostMapping
    public ApiResponse<Void> addDeclaration(@RequestBody DeclarationMemberRequest declarationMemberRequest, HttpServletRequest req) {
        AddDeclarationVo vo = new AddDeclarationVo(declarationMemberRequest, getClientIp(req));
        declarationService.declaration(vo);
        return ApiResponse.success(null);
    }



    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 여러 IP가 있을 경우 첫 번째가 클라이언트 IP
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getRemoteAddr();
        return ip;
    }
}
