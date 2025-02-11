package kimp.scrap.component.impl.exchange;

import kimp.scrap.dto.coinone.CoinoneNoticeDto;
import kimp.scrap.dto.coinone.CoinoneNoticeResultDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

public class CoinoneScrapTest {
    private RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("Coinone Notice data를 잘 받아오는 지 test - pin")
    public void coinoneNoticePinDataTest() throws Exception {

        URI uri = new URI("https://i1.coinone.co.kr/api/talk/notice/pin/?page_size=10");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "*/*");
        headers.set("referer", "https://coinone.co.kr/");
        headers.set("origin", "https://coinone.co.kr");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<CoinoneNoticeDto> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                CoinoneNoticeDto.class
        );

        CoinoneNoticeDto dto = response.getBody();
//        System.out.println(dto.getCount());
//        System.out.println(dto.getNext());
//        System.out.println(dto.getCount());

        List<CoinoneNoticeResultDto> coinoneNoticeResultDto = dto.getResults();

        for (int i = 0; i < coinoneNoticeResultDto.size(); i++) {
            System.out.println(coinoneNoticeResultDto.get(i).getTitle());
        }


    }

    @Test
    @DisplayName("Coinone Notice data를 잘 받아오는 지 test - data")
    public void coinoneNoticeDataTest() throws Exception {

        URI uri = new URI("https://i1.coinone.co.kr/api/talk/notice/?searchType=0&page=1&page_size=10");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "*/*");
        headers.set("referer", "https://coinone.co.kr/");
        headers.set("origin", "https://coinone.co.kr");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<CoinoneNoticeDto> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                CoinoneNoticeDto.class
        );

        CoinoneNoticeDto dto = response.getBody();
//        System.out.println(dto.getCount());
//        System.out.println(dto.getNext());
//        System.out.println(dto.getCount());

        List<CoinoneNoticeResultDto> coinoneNoticeResultDto = dto.getResults();

        for (int i = 0; i < coinoneNoticeResultDto.size(); i++) {
            System.out.println(coinoneNoticeResultDto.get(i).getTitle());
        }


    }
}
