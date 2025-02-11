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

        log.info("\n" + "========= [ {} ] {} =========" + "\n" +
                        ">>> content-type   : {}" + "\n" +
                        ">>> authorization  : {}" + "\n" +
                        ">>> member-agent     : {}" + "\n" +
                        ">>> host           : {}" + "\n" +
                        ">>> content-length : {}"
                , req.getMethod(), req.getRequestURI(),
                req.getHeader("content-type"),
                req.getHeader("authorization"),
                req.getHeader("member-agent"),
                req.getHeader("host"),
                req.getHeader("content-length")
        );

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
                    log.info(">>> Response-body too large, skipping.");
                }else{
                    log.info(">>> response-body : {}", formatResponseBody(responseBody));
                }
                wrappedRes.copyBodyToResponse(); // 캐시된 응답 본문을 실제 응답에 복사
            }
        }

        long end = System.currentTimeMillis();
        log.info(">>> spend time     : {} sec", (end-srt) / 1000.0);
    }

    public String formatResponseBody(String responseBody){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Object json = objectMapper.readValue(responseBody, Object.class);
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            return objectWriter.writeValueAsString(json);
        }catch(Exception e){
            log.info("hlsdflasdflkadslgfaldskglaksdlf {}", responseBody);
            return responseBody;
        }

    }


}
