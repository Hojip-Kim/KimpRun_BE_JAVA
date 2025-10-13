package kimp.config.application;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        // JsonFactory 버퍼 크기를 늘려서 큰 응답 처리
        JsonFactory jsonFactory = JsonFactory.builder()
                .build();

        ObjectMapper objectMapper = new ObjectMapper(jsonFactory);
        // Java 8 날짜/시간 타입 지원을 위한 모듈 등록
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }
}
