package kimp.scrap.component.impl.exchange;

import kimp.scrap.dto.upbit.UpbitNoticeDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class UpbitScrapTest {

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    @DisplayName("upbit notice data를 잘 불러오는지 test")
    public void NoticeAPITest() throws IOException, URISyntaxException {
        URI upbitNoticeUrl = new URI("https://api-manager.upbit.com/api/v1/announcements?os=web&page=1&per_page=30&category=all");
        UpbitNoticeDto noticeData =       restTemplate.getForObject(upbitNoticeUrl, UpbitNoticeDto.class);
        System.out.println("noticeData : " + noticeData.success);
        System.out.println("inner notice Data : " +  noticeData.data.total_pages);
        System.out.println("inner data : " + noticeData.data.getNotices().size());
    }

}
