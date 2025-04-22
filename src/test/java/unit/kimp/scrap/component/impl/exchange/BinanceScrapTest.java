package unit.kimp.scrap.component.impl.exchange;

import kimp.exchange.dto.binance.*;
import kimp.market.Enum.MarketType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

public class BinanceScrapTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    // 테스트 전용 하드코딩 엔드포인트
    private final String gatewayUrl = "http://localhost:8080/binance/notices";
    private final String noticeUrl  = "https://api.binance.com/api/v3/notice";

    @BeforeEach
    void setup() {
        // RestTemplate 과 Mock 서버 초기화
        restTemplate = new RestTemplate();
        mockServer  = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("BinanceNoticeDto JSON 매핑 및 DTO 계층 검증")
    void testBinanceNoticeDtoMapping() throws Exception {
        // 1) ExchangeNoticeDto 구조에 맞춘 샘플 JSON (2단계 중첩)
        String sampleJson = """
        {
          "data": {
            "code": "200000",
            "message": "success",
            "messageDetail": "detail",
            "success": true,
            "data": {
              "catalogs": [
                {
                  "catalogId": 1,
                  "parentCatalogId": null,
                  "icon": "icon.png",
                  "catalogName": "News",
                  "description": "Latest news",
                  "catalogType": 0,
                  "total": 1,
                  "articles": [
                    {
                      "id": 100,
                      "code": "ART100",
                      "title": "Binance Launches New Feature",
                      "type": 1,
                      "releaseDate": 1682000000000
                    }
                  ],
                  "catalogs": []
                }
              ]
            }
          },
          "absoluteUrl": "http://dummy",
          "marketType": "BINANCE"
        }
        """;

        // 2) MockRestServiceServer에 시나리오 등록
        mockServer.expect(requestTo(new URI(gatewayUrl)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"url\":\"" + noticeUrl + "\"}"))
                .andRespond(withSuccess(sampleJson, MediaType.APPLICATION_JSON));

        // 3) 실제 호출: POST { "url": noticeUrl }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        ResponseEntity<BinanceNoticeDto> response = restTemplate.exchange(
                new URI(gatewayUrl),
                HttpMethod.POST,
                new HttpEntity<>(Map.of("url", noticeUrl), headers),
                BinanceNoticeDto.class
        );

        // 4) Mock 서버가 호출되었는지 검증
        mockServer.verify();

        // 5) 최상위 DTO 검증
        BinanceNoticeDto dto = response.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getAbsoluteUrl()).isEqualTo("http://dummy");
        assertThat(dto.getMarketType()).isEqualTo(MarketType.BINANCE);

        // 6) 내부(Inner) DTO 검증
        BinanceNoticeInnerDto inner = dto.getData();
        assertThat(inner.getCode()).isEqualTo("200000");
        assertThat(inner.getMessage()).isEqualTo("success");
        assertThat(inner.getMessageDetail()).isEqualTo("detail");
        assertThat(inner.isSuccess()).isTrue();

        // 7) data.catalogs 검증
        BinanceNoticeDataDto data = inner.getData();
        List<BinanceCatalogsDto> catalogs = data.getCatalogs();
        assertThat(catalogs).hasSize(1);

        BinanceCatalogsDto catalog = catalogs.get(0);
        assertThat(catalog.getCatalogId()).isEqualTo(1);
        assertThat(catalog.getCatalogName()).isEqualTo("News");
        assertThat(catalog.getTotal()).isEqualTo(1);

        // 8) articles 검증
        List<BinanceArticleDto> articles = catalog.getArticles();
        assertThat(articles).hasSize(1);

        BinanceArticleDto article = articles.get(0);
        assertThat(article.getId()).isEqualTo(100);
        assertThat(article.getCode()).isEqualTo("ART100");
        assertThat(article.getTitle()).isEqualTo("Binance Launches New Feature");
        assertThat(article.getType()).isEqualTo(1);

        // 9) releaseDate → ZonedDateTime 변환 검증
        Instant instant = Instant.ofEpochMilli(article.getReleaseDate());
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        assertThat(zdt.toInstant().toEpochMilli()).isEqualTo(1682000000000L);
    }
}
