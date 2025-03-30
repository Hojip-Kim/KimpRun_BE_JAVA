package unit.kimp.scrap.component.impl.exchange;

import kimp.exchange.dto.coinone.CoinoneNoticeDto;
import kimp.exchange.dto.coinone.CoinoneNoticeResultDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CoinoneScrapTest {
    private RestTemplate restTemplate = new RestTemplate();

    private String coinoneNoticeUrl;
    private String coinoneReferer;
    private String coinoneOrigin;

    @BeforeEach
    @DisplayName("환경변수 설정")
    public void callEnvironmentValue(){
        Yaml yaml = new Yaml();
        try{
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.yml");
            if(inputStream != null){

                Map<String, Object> props = yaml.load(inputStream);

                Map<String, Object> coinone = (Map<String, Object>) props.get("coinone");
                Map<String, Object> url = (Map<String, Object>) coinone.get("notice");
                this.coinoneNoticeUrl= (String) url.get("url");

                this.coinoneReferer = (String) coinone.get("referer");
                this.coinoneOrigin = (String) coinone.get("origin");

            } else {
                throw new IllegalStateException("application.yml not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load application.yml", e);
        }
    }

    @Test
    @DisplayName("Coinone Notice data를 잘 받아오는 지 test - pin")
    public void coinoneNoticePinDataTest() throws Exception {

        URI uri = new URI(coinoneNoticeUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Accept", "*/*");
        headers.set("referer", coinoneReferer);
        headers.set("origin", coinoneOrigin);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<CoinoneNoticeDto> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                CoinoneNoticeDto.class
        );

        CoinoneNoticeDto dto = response.getBody();

        List<CoinoneNoticeResultDto> coinoneNoticeResultDto = dto.getResults();

        for (int i = 0; i < coinoneNoticeResultDto.size(); i++) {
            System.out.println(coinoneNoticeResultDto.get(i).getTitle());
            System.out.println(coinoneNoticeResultDto.get(i).getCreatedAt());
            System.out.println(coinoneNoticeResultDto.get(i).getGet_absolute_url());
        }


    }
}
