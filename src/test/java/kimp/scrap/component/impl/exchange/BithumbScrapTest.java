package kimp.scrap.component.impl.exchange;

import kimp.scrap.dto.bithumb.BithumbNoticeDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BithumbScrapTest {

    private RestTemplate restTemplate;

    @Test
    @DisplayName("빗썸 공지사항 데이터 가져오기")
    public void fetchBithumbNotices() {
        try {
            HttpHeaders headers = new HttpHeaders();
            // JSON 요청이므로 Content-Type은 application/json
            headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            URI localUrl = new URI("http://localhost:3000/api/controller-gateway");

            // JSON 형식으로 요청 본문 구성
            Map<String, String> body = new HashMap<>();
            body.put("url", "https://feed.bithumb.com/notice");

            // 첫번째 인자는 body, 두번째 인자는 headers (순서 주의)
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            restTemplate = new RestTemplate(factory);

            ResponseEntity<BithumbNoticeDto> res = restTemplate.exchange(
                    localUrl,
                    HttpMethod.POST,
                    entity,
                    BithumbNoticeDto.class
            );

            String bithumbNoticeHtml = res.getBody().getData();

            Document document = Jsoup.parse(bithumbNoticeHtml);

            Elements noticeTitles = document.select(".NoticeContentList_notice-list__link-title__nlmSC");
            Elements noticeDate = document.select(".NoticeContentList_notice-list__link-date__gDc6U");

            String[] noticeArray = new String[noticeTitles.size()];

            int i = 0;

            for(Element title : noticeTitles) {
                noticeArray[i] = title.text();
                i++;
            }
            i = 0;
            for(Element date : noticeDate ) {
                noticeArray[i] += " " + date.text();
                i++;
            }

            System.out.println(Arrays.toString(noticeArray));


        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
