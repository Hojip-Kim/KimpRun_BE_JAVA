package unit.kimp.market.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.components.Dollar;
import kimp.market.components.impl.market.Upbit;
import kimp.market.service.serviceImpl.MarketInfoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DollarTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Dollar dollar;

    @Mock
    private Upbit upbit;

    @InjectMocks
    private MarketInfoServiceImpl marketInfoService;

    private final String sampleJson = """
      {  
        \"result\":\"success\",
        \"provider\":\"https://www.exchangerate-api.com\",
        \"base_code\":\"USD\",
        \"rates\":{ "USD":1,"KRW":1419.444209, "EUR":0.868883 }
      }
      """;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("USD-KRW 환율 조회: 예상값 반환")
    void shouldReturnExpectedUsdKrwRate() throws Exception {
        // 1) Dollar mock 설정
        when(dollar.getApiDollar()).thenReturn(1419.444209);

        // 2) Upbit mock 설정 (null 방지)
        when(upbit.getUpbitTether()).thenReturn(java.math.BigDecimal.ONE);

        // 3) 초기화 메서드 호출 (dollarKRW 필드 설정)
        marketInfoService.init();

        // 4) 메서드 호출 및 검증
        double rate = marketInfoService.getDollarKRW();
        assertEquals(1419.444209, rate, 1e-6, "KRW 환율이 예상값과 일치해야 합니다.");
    }
}
