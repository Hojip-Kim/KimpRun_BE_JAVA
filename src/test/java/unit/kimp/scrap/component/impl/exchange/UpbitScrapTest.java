package unit.kimp.scrap.component.impl.exchange;

import kimp.exchange.dto.upbit.UpbitNoticeDto;
import kimp.exchange.dto.upbit.UpbitDataDto;
import kimp.exchange.dto.upbit.UpbitNoticeDataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

public class UpbitScrapTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private final String gatewayUrl = "http://localhost:8080/upbit/notices";

    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("UpbitNoticeDto 새로운 샘플 JSON 매핑 및 DTO 검증")
    void testUpbitNoticeDtoMapping() throws Exception {
        String sampleJson = """
        {
          "success": true,
          "data": {
            "total_pages": 149,
            "total_count": 4460,
            "notices": [
              {"listed_at":"2025-04-22T15:21:10+09:00","first_listed_at":"2025-04-11T00:18:36+09:00","id":5030,"title":"네트워크 이슈에 따른 밸리디티(VAL) 입출금 일시 중단 안내 (완료)","category":"입출금","need_new_badge":false,"need_update_badge":true},
              {"listed_at":"2025-04-22T14:27:58+09:00","first_listed_at":"2025-04-22T14:27:58+09:00","id":5056,"title":"상담 서비스 일시 중단 안내 (04/28 03:00 ~)","category":"점검","need_new_badge":true,"need_update_badge":false},
              {"listed_at":"2025-04-22T13:58:32+09:00","first_listed_at":"2025-04-22T13:58:32+09:00","id":5055,"title":"딥북(DEEP) KRW 마켓 디지털 자산 추가","category":"거래","need_new_badge":true,"need_update_badge":false}
            ],
            "fixed_notices": []
          }
        }
        """;

        mockServer.expect(requestTo(new URI(gatewayUrl)))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, containsString(MediaType.APPLICATION_JSON_VALUE)))
                .andRespond(withSuccess(sampleJson, MediaType.APPLICATION_JSON));

        ResponseEntity<UpbitNoticeDto> response = restTemplate.getForEntity(
                gatewayUrl,
                UpbitNoticeDto.class
        );

        mockServer.verify();

        UpbitNoticeDto dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.isSuccess()).isTrue();

        UpbitDataDto data = dto.getData();
        assertThat(data.getTotal_pages()).isEqualTo(149);
        assertThat(data.getTotal_count()).isEqualTo(4460);

        List<UpbitNoticeDataDto> notices = data.getNotices();
        assertThat(notices).hasSize(3);

        // 첫 번째 항목 검증
        UpbitNoticeDataDto first = notices.get(0);
        assertThat(first.getId()).isEqualTo(5030);
        assertThat(first.getTitle()).startsWith("네트워크 이슈에 따른 밸리디티");
        assertThat(first.getCategory()).isEqualTo("입출금");
        assertThat(first.isNeed_new_badge()).isFalse();
        assertThat(first.isNeed_update_badge()).isTrue();

        // 날짜 파싱 검증
        OffsetDateTime expectedListed = OffsetDateTime.parse("2025-04-22T15:21:10+09:00");
        assertThat(first.getListed_at()).isEqualTo(expectedListed);
        OffsetDateTime expectedFirstListed = OffsetDateTime.parse("2025-04-11T00:18:36+09:00");
        assertThat(first.getFirst_listed_at()).isEqualTo(expectedFirstListed);

        // 두 번째 항목 검증
        UpbitNoticeDataDto second = notices.get(1);
        assertThat(second.getId()).isEqualTo(5056);
        assertThat(second.getCategory()).isEqualTo("점검");
        assertThat(second.isNeed_new_badge()).isTrue();
        assertThat(second.isNeed_update_badge()).isFalse();

        // 세 번째 항목 검증
        UpbitNoticeDataDto third = notices.get(2);
        assertThat(third.getId()).isEqualTo(5055);
        assertThat(third.getCategory()).isEqualTo("거래");
    }
}
