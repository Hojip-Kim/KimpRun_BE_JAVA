package unit.kimp.scrap.component.impl.exchange;

import kimp.exchange.dto.binance.BinanceNoticeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
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

    private String localGateway;
    private String binanceNoticeUrl;

    @BeforeEach
    @DisplayName("환경변수 설정")
    public void callEnvironmentValue(){
        Yaml yaml = new Yaml();
        try{
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.yml");
            if(inputStream != null){

                Map<String, Object> props = yaml.load(inputStream);
                Map<String, Object> admin = (Map<String, Object>) props.get("admin");
                Map<String, Object> binance = (Map<String, Object>) props.get("binance");
                Map<String, Object> notice = (Map<String, Object>) binance.get("notice");
                this.localGateway = (String) admin.get("gateway");
                this.binanceNoticeUrl = (String) notice.get("url");

            } else {
                throw new IllegalStateException("application.yml not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load application.yml", e);
        }
    }

    @Test
    @DisplayName("바이낸스 공지사항 데이터 가져오기")
    public void fetchBinanceNotices() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            URI localUrl = new URI(localGateway);

            Map<String, String> body = new HashMap<>();
            body.put("url", binanceNoticeUrl);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<BinanceNoticeDto> res = restTemplate.exchange(
                    localUrl,
                    HttpMethod.POST,
                    entity,
                    BinanceNoticeDto.class
            );
            String title = res.getBody().getData().getData().getCatalogs().get(0).getArticles().get(0).getTitle();
            Long date = res.getBody().getData().getData().getCatalogs().get(0).getArticles().get(0).getReleaseDate();
            String link = res.getBody().getData().getData().getCatalogs().get(0).getArticles().get(0).getCode();


            Instant instant = Instant.ofEpochMilli(date);
            ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());


            System.out.println("title " + title);
            System.out.println("date " + dateTime);
            System.out.println("aLink " + link);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
