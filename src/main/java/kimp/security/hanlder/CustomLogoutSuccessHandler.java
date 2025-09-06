package kimp.security.hanlder;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kimp.exception.response.ApiResponse;
import kimp.user.dto.response.LogoutResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        // 1. SecurityContext 정리
        SecurityContextHolder.clearContext();

        // 2. HTTP 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionId = session.getId();
            session.invalidate();
        }
        
        // 3. 응답 설정
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        LogoutResponseDto logoutResponseDto = new LogoutResponseDto("success", "로그아웃에 성공하였습니다.");
        ApiResponse<LogoutResponseDto> apiResponse = ApiResponse.success(logoutResponseDto);

        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(apiResponse);
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
