package kimp.unit.kimp.market.component;


import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.components.Dollar;
import kimp.market.dto.response.DollarResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {Dollar.class})
public class DollarTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${dollar.api.url}")
    private String dollarApiUrl;

    @Test
    @DisplayName("dollar 데이터 Fetch가 잘 되는지에 대한 Test.")
    void fetchDollarData() throws IOException {
        String data = restTemplate.getForObject(dollarApiUrl, String.class);

        DollarResponseDto dollarDto = objectMapper.readValue(data, DollarResponseDto.class);

        // 데이터가 null이 아닌지 확인
        assertNotNull(dollarDto, "DollarResponseDto 데이터가 null입니다.");

        DollarResponseDto.Rates rates = dollarDto.getRates();
        assertNotNull(rates, "Rates 데이터가 null입니다.");

        // KRW 환율이 0보다 큰지 확인
        double krwRate = dollarDto.getRates().getKRW();

        assertTrue(krwRate > 0, "KRW 환율이 0보다 커야 합니다.");
    }

}
