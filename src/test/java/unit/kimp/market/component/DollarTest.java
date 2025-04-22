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

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private final String sampleJson = """
      {  
        \"result\":\"success\",
        \"provider\":\"https://www.exchangerate-api.com\",
        \"base_code\":\"USD\",
        \"rates\":{ "USD":1,"KRW":1419.444209, "EUR":0.868883 }
      }
      """;

    @BeforeEach
    void setUp() throws Exception {
        // dollarUrl 필드를 리플렉션으로 설정
        Field urlField = Dollar.class.getDeclaredField("dollarUrl");
        urlField.setAccessible(true);
        urlField.set(dollar, "http://dummy-url.com/api");
    }

    @Test
    @DisplayName("getUSDKRW: RestTemplate+ObjectMapper Mock 후 KRW 환율 반환 검증")
    void getUSDKRW_returnsExpectedRate() throws Exception {
        // 1) RestTemplate 호출 스텁
        when(restTemplate.getForObject(eq("http://dummy-url.com/api"), eq(String.class)))
                .thenReturn(sampleJson);

        // 2) ObjectMapper 매핑 스텁
        DollarResponseDto fakeDto = new DollarResponseDto();
        DollarResponseDto.Rates rates = new DollarResponseDto.Rates();
        Field usdField = DollarResponseDto.Rates.class.getDeclaredField("USD");
        Field krwField = DollarResponseDto.Rates.class.getDeclaredField("KRW");
        usdField.setAccessible(true);
        krwField.setAccessible(true);
        usdField.set(rates, 1.0);
        krwField.set(rates, 1419.444209);
        Field ratesField = DollarResponseDto.class.getDeclaredField("rates");
        ratesField.setAccessible(true);
        ratesField.set(fakeDto, rates);

        when(objectMapper.readValue(eq(sampleJson), eq(DollarResponseDto.class)))
                .thenReturn(fakeDto);

        // 3) 메서드 호출 및 검증
        double rate = dollar.getUSDKRW();
        assertEquals(1419.444209, rate, 1e-6, "KRW 환율이 예상값과 일치해야 합니다.");
    }
}
