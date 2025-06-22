package kimp.market.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.dto.market.response.DollarResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class Dollar {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Value("${dollar.api.url}")
    private String dollarUrl;

    public Dollar(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public double getApiDollar() throws JsonProcessingException {
        log.info("dollar 정보 업데이트 성공");
        String data = restTemplate.getForObject(dollarUrl, String.class);

        DollarResponseDto dollarDto = objectMapper.readValue(data, DollarResponseDto.class);

        if(dollarDto == null){
            throw new IllegalCallerException("Dollar Data is null.");
        }
        return dollarDto.getRates().getKRW();
    }

}
