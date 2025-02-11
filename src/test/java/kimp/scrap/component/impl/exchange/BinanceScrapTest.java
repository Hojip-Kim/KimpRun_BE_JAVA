package kimp.scrap.component.impl.exchange;

import kimp.scrap.dto.binance.BinanceNotice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BinanceScrapTest {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${admin.gateway}")
    private String localGateway;

    @Test
    @DisplayName("바이낸스 공지사항 데이터 가져오기")
    public void fetchBinanceNotices() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            URI localUrl = new URI("http://localhost:3000/api/controller-gateway");

            Map<String, String> body = new HashMap<>();
            body.put("url", "https://www.binance.com/bapi/apex/v1/public/apex/cms/article/list/query?type=1&pageNo=1&pageSize=10&catalogId=48");

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            restTemplate = new RestTemplate(factory);

            ResponseEntity<BinanceNotice> res = restTemplate.exchange(
                    localUrl,
                    HttpMethod.POST,
                    entity,
                    BinanceNotice.class
            );
            String title = res.getBody().getData().getData().getCatalogs().get(0).getArticles().get(0).getTitle();
            Long date = res.getBody().getData().getData().getCatalogs().get(0).getArticles().get(0).getReleaseDate();

            Instant instant = Instant.ofEpochMilli(date);
            ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());


            System.out.println("title " + title);
            System.out.println("date " + dateTime);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
