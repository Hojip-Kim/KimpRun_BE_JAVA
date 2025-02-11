package kimp.scrap.component.impl.exchange;

import kimp.scrap.dto.bithumb.BithumbNoticeDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("bithumbScrap")
public class BithumbScrap extends ExchangeScrapAbstract<BithumbNoticeDto> {

    @Value("${client.url.request}")
    private String clientRequestUrl;

    @Value("${bithumb.url.notice}")
    private String bithumbNoticeUrl;

    private final RestTemplate restTemplate;

    public BithumbScrap(RestTemplate restTemplate, StringRedisTemplate stringRedisTemplate, @Qualifier("redisTemplate") RedisTemplate redisTemplate) {
        super(restTemplate, stringRedisTemplate);
        this.restTemplate = restTemplate;
    }

    /* bithumb는 html 자체를 반환하는 SSR형태의 Application이기 때문에 String class를 반환
    * 별도의 parse를 해주어야 함 (jsoup를 통한 parse)
    * */
    @Override
    protected Class<BithumbNoticeDto> getResponseType() {
        return BithumbNoticeDto.class;
    }

    @Override
    protected void setNoticeToRedis(){
    }

    @Override
    protected void getNoticeFromRedis() {

    }

    @Override
    protected String getNewNotice() {
        return "";
    }

    @Override
    protected BithumbNoticeDto getNoticeData() {

        return null;
    }

    /*
    * Scheduling을 통한 bithumb 데이터 추출
    * cloud flare우회를 위한 클라이언트 단에서의 html소스 추출, 서버로의 전달
    * */

    public void fetchBithumbNotices() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            URI localUrl = new URI(clientRequestUrl);

            Map<String, String> body = new HashMap<>();
            body.put("url", bithumbNoticeUrl);
            HttpEntity<Map<String, String>> entity =new HttpEntity<>(body, headers);

            ResponseEntity<BithumbNoticeDto> res = restTemplate.exchange(
                    localUrl,
                    HttpMethod.POST,
                    entity,
                    BithumbNoticeDto.class
            );

            String bithumbNoticeHtml = res.getBody().getData();

            Document document = Jsoup.parse(bithumbNoticeHtml);

            Elements noticeTitles = document.select(".NoticeContentList_notice-list__link-title__nlmSC");

            for (Element titleElement : noticeTitles) {
                System.out.println(titleElement.text());
            }


        }catch(URISyntaxException e){
            e.printStackTrace();
        }
    }

//    // 공지사항 페이지 html에서 각각의 게시물 제목을 String형태로 parsing하는 method.
//    private List<String> parseHtmlDocument(Document doc) {
//
//    }

/*
public class BithumbScrapTest {

    private RestTemplate restTemplate;

    @Test
    @DisplayName("쿠키 및 헤더를 포함하여 빗썸 공지사항 데이터 가져오기")
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

            for (Element titleElement : noticeTitles) {
                System.out.println(titleElement.text());
            }


        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
*/

}
