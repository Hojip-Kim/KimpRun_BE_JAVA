package unit.kimp.scrap.component.impl.exchange;

import kimp.exchange.dto.bithumb.BithumbNoticeDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * 순수 단위 테스트: 실제 HTTP 서버를 띄우지 않고
 * MockRestServiceServer 로 RestTemplate 호출을 모킹하며,
 * Jsoup 파싱 로직을 검증합니다.
 */
public class BithumbScrapTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;

    // 테스트용 하드코딩 엔드포인트
    private final String gatewayUrl      = "http://localhost:8080/bithumb/notices";
    private final String bithumbNoticeUrl = "https://api.bithumb.com/notice";

    @BeforeEach
    void setup() {
        this.restTemplate = new RestTemplate();
        this.mockServer  = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("BithumbNoticeDto JSON → HTML 파싱 단위 테스트")
    void testBithumbNoticeParsing() throws Exception {
        // 1) 샘플 HTML (Jsoup 파싱 대상)
        String sampleHtml = """
          <div class="NoticeContentList_notice-list__link-title__nlmSC">TitleOne</div>
          <div class="NoticeContentList_notice-list__link-date__gDc6U">2025-04-21</div>
          <div class="NoticeContentList_notice-list__i337r"><a href="/notice/1">LinkOne</a></div>
          <div class="NoticeContentList_notice-list__link-title__nlmSC">TitleTwo</div>
          <div class="NoticeContentList_notice-list__link-date__gDc6U">2025-04-20</div>
          <div class="NoticeContentList_notice-list__i337r"><a href="/notice/2">LinkTwo</a></div>
          """;

        // 2) Mock JSON 응답 (ExchangeNoticeDto<String> 형식)
        String sampleJson = """
          {
            "data": %s
          }
          """.formatted(
                // JSON 문자열 안에 sampleHtml을 이스케이프 없이 넣으려면
                "\"" + sampleHtml.replace("\"", "\\\"").replace("\n", "") + "\""
        );

        // 3) MockRestServiceServer 시나리오 등록
        mockServer.expect(requestTo(new URI(gatewayUrl)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("{\"url\":\"" + bithumbNoticeUrl + "\"}"))
                .andRespond(withSuccess(sampleJson, MediaType.APPLICATION_JSON));

        // 4) 실제 호출: POST { "url": bithumbNoticeUrl }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        ResponseEntity<BithumbNoticeDto> response = restTemplate.exchange(
                new URI(gatewayUrl),
                HttpMethod.POST,
                new HttpEntity<>(Map.of("url", bithumbNoticeUrl), headers),
                BithumbNoticeDto.class
        );

        // 5) Mock 서버 호출 검증
        mockServer.verify();

        // 6) DTO에서 HTML 추출
        BithumbNoticeDto dto = response.getBody();
        assertThat(dto).isNotNull();
        String html = dto.getData();
        assertThat(html).contains("TitleOne", "TitleTwo");

        // 7) Jsoup 파싱
        Document doc = Jsoup.parse(html);
        Elements titles = doc.select(".NoticeContentList_notice-list__link-title__nlmSC");
        Elements dates  = doc.select(".NoticeContentList_notice-list__link-date__gDc6U");
        Elements links  = doc.select(".NoticeContentList_notice-list__i337r a");

        // 8) 파싱 결과 검증
        assertThat(titles).hasSize(2);
        assertThat(titles.get(0).text()).isEqualTo("TitleOne");
        assertThat(titles.get(1).text()).isEqualTo("TitleTwo");

        assertThat(dates).hasSize(2);
        assertThat(dates.get(0).text()).isEqualTo("2025-04-21");
        assertThat(dates.get(1).text()).isEqualTo("2025-04-20");

        assertThat(links).hasSize(2);
        assertThat(links.get(0).attr("href")).isEqualTo("/notice/1");
        assertThat(links.get(1).attr("href")).isEqualTo("/notice/2");
    }
}
