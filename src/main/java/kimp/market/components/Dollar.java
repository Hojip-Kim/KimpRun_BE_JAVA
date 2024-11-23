package kimp.market.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.dto.response.DollarResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class Dollar {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Value("${dollar.api.url}")
    private String dollarUrl;

    public Dollar(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Bean
    public double getUSDKRW() throws IOException {
        String data = restTemplate.getForObject(dollarUrl, String.class);

        DollarResponseDto dollarDto = objectMapper.readValue(data, DollarResponseDto.class);

        if(dollarDto == null){
            throw new IllegalCallerException("Dollar Data is null.");
        }

        return dollarDto.getRates().getKRW();
    }
}
