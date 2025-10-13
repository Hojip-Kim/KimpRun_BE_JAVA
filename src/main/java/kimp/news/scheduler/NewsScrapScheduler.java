package kimp.news.scheduler;

import kimp.news.service.NewsScrapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewsScrapScheduler {

    private final NewsScrapService newsScrapService;

    public NewsScrapScheduler(NewsScrapService newsScrapService) {
        this.newsScrapService = newsScrapService;
    }

    @Scheduled(fixedDelay = 60000) // 1분마다 실행
    public void scrapBloomingBitNews() {
        log.info("블루밍비트 뉴스 스케줄 스크래핑 시작");
        try {
            newsScrapService.scrapBloomingBitNews();
        } catch (Exception e) {
            log.error("블루밍비트 뉴스 스케줄 스크래핑 중 오류 발생", e);
        }
    }
}
