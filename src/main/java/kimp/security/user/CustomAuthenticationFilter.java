package kimp.security.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kimp.security.user.dto.LoginResponseDto;
import kimp.user.entity.Member;
import kimp.user.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final SecurityContextRepository securityContextRepository;

    private final MemberService memberService;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, MemberService memberService) {
        super();
        setAuthenticationManager(authenticationManager); // AuthenticationManager 설정
        setFilterProcessesUrl("/login");
        this.securityContextRepository = new HttpSessionSecurityContextRepository();
        this.memberService = memberService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> credentials = mapper.readValue(request.getInputStream(), Map.class);
            String membername = credentials.get("email");
            String password = credentials.get("password");

            log.info("로그인 시도 - membername: {}", membername);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(membername, password);

            return this.getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            log.error("로그인 시도 중 오류 발생", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        super.unsuccessfulAuthentication(request, response, failed);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"result\":\"failure\",\"message\":\"" + failed.getMessage() + "\"}");
        response.getWriter().flush();

        log.info("로그인 실패 - 이유: {}", failed.getMessage());
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException {


        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

        ObjectMapper mapper = new ObjectMapper();

        String ip = getIpFromRequestHeader(request);

        log.info("로그인 성공 - memberName: {}", customUserDetails.getEmail());

        Member member = memberService.getmemberByEmail(customUserDetails.getEmail());

        LoginResponseDto loginResponseDto = new LoginResponseDto();

        if(memberService.isFirstLogin(member)) {

            memberService.setMemberIP(member, ip);
            loginResponseDto.setResult("success");
            loginResponseDto.setMessage("로그인에 성공하였습니다.");

        }else{
            if(!memberService.isEqualIpBeforeLogin(member, ip)){
                String beforeIp = member.getMemberAgent().getIp();

                loginResponseDto.setResult("check");
                loginResponseDto.setMessage("IP 확인 필요");
                loginResponseDto.setData(beforeIp);

            }else{
                loginResponseDto.setResult("success");
                loginResponseDto.setMessage("로그인에 성공하였습니다.");
            }
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);

        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);

        response.setStatus(HttpServletResponse.SC_OK);

        response.setContentType("application/json;charset=UTF-8");

        String jsonResponse = mapper.writeValueAsString(loginResponseDto);

        response.getWriter().write(jsonResponse);

        response.getWriter().flush();
    }

    public String getIpFromRequestHeader(HttpServletRequest request) {
        String[] headersToCheck = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
        };

        for(String header : headersToCheck) {
            String ip = request.getHeader(header);
            if(ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

}
