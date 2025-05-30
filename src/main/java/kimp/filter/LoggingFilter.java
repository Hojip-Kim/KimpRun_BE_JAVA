package kimp.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// Request per 단위 Filter

@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    private static final long MAX_LOG_LENGTH = 1000;

    public String maskSensitiveData(String requestBody) {
        return requestBody.replaceAll("\"password\"\\s*:\\s*\"(.*?)\"", "\"password\":\"****\"");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        long srt = System.currentTimeMillis();
        ContentCachingRequestWrapper wrappedReq = new ContentCachingRequestWrapper(req);
        ContentCachingResponseWrapper wrappedRes = new ContentCachingResponseWrapper(res);

        String response = "";

        try {
            filterChain.doFilter(wrappedReq, wrappedRes);
        } catch (Exception e) {
            log.warn("logging error", e);
        } finally {
            String requestBody = new String(wrappedReq.getContentAsByteArray(), StandardCharsets.UTF_8).trim();

            if(!requestBody.isEmpty()) {
                requestBody = maskSensitiveData(requestBody);
                log.info(">>> request-body : {}", requestBody);
            }

            byte[] contentAsByteArray = wrappedRes.getContentAsByteArray();
            if(contentAsByteArray.length > 0) {
                String responseBody = new String(contentAsByteArray, StandardCharsets.UTF_8).trim();

                // response body가 너무 크면 skip
                if(responseBody.length() > MAX_LOG_LENGTH){
                    response = "response-body : too large, skipped.";
                }else{
                    response = "response-body : " + responseBody;
                }
                wrappedRes.copyBodyToResponse(); // 캐시된 응답 본문을 실제 응답에 복사
            }
        }

        log.info("\n" + "Http Method : [ {} ] End-Point : [ {} ] Content-Type : [ {} ] Authorization  : [ {} ] User-agent : [ {} ] Host : [ {} ] Content-length : [ {} ] Response-body : [ {} ]"
                , req.getMethod(), req.getRequestURI(),
                req.getHeader("content-type"),
                req.getHeader("authorization"),
                req.getHeader("member-agent"),
                req.getHeader("host"),
                req.getHeader("content-length"),
                response
        );

        long end = System.currentTimeMillis();
        log.info(">>> spend time     : {} sec", (end-srt) / 1000.0);
    }



}
