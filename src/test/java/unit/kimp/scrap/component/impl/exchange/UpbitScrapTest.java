package unit.kimp.scrap.component.impl.exchange;

import kimp.exchange.dto.upbit.UpbitNoticeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;

public class UpbitScrapTest {

    private RestTemplate restTemplate = new RestTemplate();
    private String upbitNoticeUrl;

    @BeforeEach
    @DisplayName("환경변수 설정")
    public void callEnvironmentValue(){
        Yaml yaml = new Yaml();
        try{
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.yml");
            if(inputStream != null){

                Map<String, Object> props = yaml.load(inputStream);

                Map<String, Object> upbit = (Map<String, Object>) props.get("upbit");
                Map<String, Object> notice = (Map<String, Object>) upbit.get("notice");
                this.upbitNoticeUrl = (String) notice.get("url");

            } else {
                throw new IllegalStateException("application.yml not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load application.yml", e);
        }
    }

    @Test
    @DisplayName("upbit notice data를 잘 불러오는지 test")
    public void NoticeAPITest() throws IOException, URISyntaxException {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        UpbitNoticeDto noticeData = restTemplate.getForObject(upbitNoticeUrl, UpbitNoticeDto.class);
        System.out.println("noticeData : " + noticeData.success);
        System.out.println("inner notice Data : " +  noticeData.data.total_pages);
        System.out.println("inner data : " + noticeData.data.getNotices().size());
        System.out.println("id : " + noticeData.data.getNotices().get(0).getId());
    }

}
