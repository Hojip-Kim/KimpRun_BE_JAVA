package unit.kimp.scrap.component.impl.exchange;

import kimp.exchange.dto.bithumb.BithumbNoticeDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BithumbScrapTest {

    private RestTemplate restTemplate;

    private String localGateway;
    private String bithumbNoticeUrl;

    @BeforeEach
    @DisplayName("환경변수 설정")
    public void callEnvironmentValue(){
        Yaml yaml = new Yaml();
        try{
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.yml");
            if(inputStream != null){

                Map<String, Object> props = yaml.load(inputStream);

                Map<String, Object> admin = (Map<String, Object>) props.get("admin");
                this.localGateway = (String) admin.get("gateway");

                Map<String, Object> bithumb = (Map<String, Object>) props.get("bithumb");
                Map<String, Object> notice = (Map<String, Object>) bithumb.get("notice");
                this.bithumbNoticeUrl= (String) notice.get("url");

            } else {
                throw new IllegalStateException("application.yml not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load application.yml", e);
        }
    }

    @Test
    @DisplayName("빗썸 공지사항 데이터 가져오기")
    public void fetchBithumbNotices() {
        try {
            HttpHeaders headers = new HttpHeaders();
            // JSON 요청이므로 Content-Type은 application/json
            headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            URI localUrl = new URI(localGateway);

            // JSON 형식으로 요청 본문 구성
            Map<String, String> body = new HashMap<>();
            body.put("url", bithumbNoticeUrl);

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
            Elements aLink = document.select(".NoticeContentList_notice-list__i337r a");

            String[] noticeArray = new String[noticeTitles.size()];

            int i = 0;

            int bytes = 0;

            for(Element title : noticeTitles) {
                noticeArray[i] = title.text();
                i++;
            }
            i = 0;
            for(Element date : noticeDate ) {
                noticeArray[i] += " " + date.text();
                System.out.println(date.text());
                i++;
            }
            i = 0;

            for(Element link : aLink ) {
                noticeArray[i] += " " + link.attr("href");
                i++;
            }

            for(String elem : noticeArray) {
                bytes += elem.getBytes().length;
            }



        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
