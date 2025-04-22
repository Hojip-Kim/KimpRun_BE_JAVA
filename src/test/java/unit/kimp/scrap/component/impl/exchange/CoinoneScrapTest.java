package unit.kimp.scrap.component.impl.exchange;

import kimp.exchange.dto.coinone.CoinoneNoticeDto;
import kimp.exchange.dto.coinone.CoinoneNoticeResultDto;
import kimp.exchange.dto.coinone.CoinoneCreatedByDto;
import kimp.exchange.dto.coinone.CoinoneFlaggedContentDto;
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

public class CoinoneScrapTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private final String gatewayUrl = "http://localhost:8080/coinone/notices";

    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("Coinone 샘플 JSON 매핑 및 DTO 검증")
    void testCoinoneSampleJsonMapping() throws Exception {
        String sampleJson = """
        {
          "count": 3742,
          "next": "http://i1.coinone.co.kr/api/talk/notice/?ordering=-created_at&page=2&searchType=&searchWord=",
          "previous": null,
          "results": [
            {
              "id": 3956,
              "card_category": "입출금",
              "get_absolute_url": "/talk/notice/detail/3956/",
              "created_by": {
                "uuid": "caccd83d085a94383b9ea0ffc694241e",
                "trading_level": 1,
                "nickname": "코인원",
                "comment_count": 34,
                "thread_count": 14,
                "vote_count": 1195,
                "level": 1,
                "signature": "Bringing Blockchain into the World - 새로운 연결이 세상에 스며들다",
                "user_type": "A",
                "is_blocked": false
              },
              "flagged_content": { "status": null },
              "vote_count": 0,
              "title": "스크롤(SCR) 네트워크 업그레이드를 위한 입출금 일시 중단 안내 (4/22 14:00~)",
              "created_at": "2025-04-21T16:41:37.342654+09:00",
              "updated_at": "2025-04-21T16:41:37.342688+09:00"
            },
            { "id": 3955, "card_category": "거래지원", "get_absolute_url": "/talk/notice/detail/3955/" },
            { "id": 3954, "card_category": "당첨자발표", "get_absolute_url": "/talk/notice/detail/3954/" },
            { "id": 3952, "card_category": "보안",       "get_absolute_url": "/talk/notice/detail/3952/" },
            { "id": 3951, "card_category": "거래지원", "get_absolute_url": "/talk/notice/detail/3951/" }
          ]
        }
        """;

        mockServer.expect(requestTo(new URI(gatewayUrl)))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, containsString(MediaType.APPLICATION_JSON_VALUE)))
                .andRespond(withSuccess(sampleJson, MediaType.APPLICATION_JSON));

        ResponseEntity<CoinoneNoticeDto> response = restTemplate.getForEntity(
                gatewayUrl,
                CoinoneNoticeDto.class
        );

        mockServer.verify();

        CoinoneNoticeDto dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getCount()).isEqualTo(3742);
        assertThat(dto.getNext()).isEqualTo("http://i1.coinone.co.kr/api/talk/notice/?ordering=-created_at&page=2&searchType=&searchWord=");
        assertThat(dto.getPrevious()).isNull();

        List<CoinoneNoticeResultDto> results = dto.getResults();
        assertThat(results).hasSize(5);

        CoinoneNoticeResultDto first = results.get(0);
        assertThat(first.getId()).isEqualTo(3956);
        assertThat(first.getCard_category()).isEqualTo("입출금");
        assertThat(first.getGet_absolute_url()).isEqualTo("/talk/notice/detail/3956/");

        CoinoneCreatedByDto createdBy = first.getCreateBy();
        assertThat(createdBy.getUuid()).isEqualTo("caccd83d085a94383b9ea0ffc694241e");
        assertThat(createdBy.getTrading_level()).isEqualTo(1);
        assertThat(createdBy.getNickname()).isEqualTo("코인원");
        assertThat(createdBy.is_blocked()).isFalse();

        CoinoneFlaggedContentDto flagged = first.getFlaggedContent();
        assertThat(flagged.getStatus()).isNull();

        assertThat(first.getVote_count()).isEqualTo(0);
        assertThat(first.getTitle()).startsWith("스크롤(SCR) 네트워크 업그레이드를 위한 입출금");

        OffsetDateTime createdAt = first.getCreatedAt();
        assertThat(createdAt).isEqualTo(OffsetDateTime.parse("2025-04-21T16:41:37.342654+09:00"));
        assertThat(first.getUpdatedAt()).isEqualTo(OffsetDateTime.parse("2025-04-21T16:41:37.342688+09:00"));
    }
}
