package unit.kimp.market.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.components.Dollar;
import kimp.market.dto.market.response.DollarResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DollarTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private Dollar dollar;

    private String dollarApiUrl;

    @BeforeEach
    void setUp() throws Exception {
        // application-test.yml에서 dollar.api.url 읽어오기
        Yaml yaml = new Yaml();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("application-test.yml")) {
            if (is == null) {
                throw new IllegalStateException("application-test.yml not found in classpath");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> props = yaml.load(is);
            @SuppressWarnings("unchecked")
            Map<String, Object> dollarProps = (Map<String, Object>) props.get("dollar");
            @SuppressWarnings("unchecked")
            Map<String, Object> apiProps = (Map<String, Object>) dollarProps.get("api");
            this.dollarApiUrl = apiProps.get("url").toString();
        }

        Field urlField = Dollar.class.getDeclaredField("dollarUrl");
        urlField.setAccessible(true);
        urlField.set(dollar, dollarApiUrl);
    }

    @Test
    @DisplayName("getUSDKRW: RestTemplate/ObjectMapper 스텁 후 올바른 환율 반환 검증")
    void getUSDKRW_returnsExpectedRate() throws Exception {
        String fakeJson = "{\n" +
                "  \"result\":\"success\",\n" +
                "  \"provider\":\"MockProvider\",\n" +
                "  \"base_code\":\"USD\",\n" +
                "  \"rates\":{ \"USD\":1.0, \"KRW\":1200.75 }\n" +
                "}";
        when(restTemplate.getForObject(eq(dollarApiUrl), eq(String.class)))
                .thenReturn(fakeJson);

        DollarResponseDto fakeDto = new DollarResponseDto();
        DollarResponseDto.Rates rates = new DollarResponseDto.Rates();
        Field usdField = DollarResponseDto.Rates.class.getDeclaredField("USD");
        Field krwField = DollarResponseDto.Rates.class.getDeclaredField("KRW");
        usdField.setAccessible(true);
        krwField.setAccessible(true);
        usdField.set(rates, 1.0);
        krwField.set(rates, 1200.75);
        Field ratesField = DollarResponseDto.class.getDeclaredField("rates");
        ratesField.setAccessible(true);
        ratesField.set(fakeDto, rates);

        when(objectMapper.readValue(eq(fakeJson), eq(DollarResponseDto.class)))
                .thenReturn(fakeDto);

        // 3) 컴포넌트 메서드 호출
        double rate = dollar.getUSDKRW();
        assertEquals(1200.75, rate, 0.0001, "KRW 환율이 예상값과 일치해야 합니다.");
    }
}
