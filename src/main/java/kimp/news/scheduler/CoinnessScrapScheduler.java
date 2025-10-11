package kimp.news.scheduler;

import kimp.news.service.CoinnessScrapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CoinnessScrapScheduler {

    private final CoinnessScrapService coinnessScrapService;

    public CoinnessScrapScheduler(CoinnessScrapService coinnessScrapService) {
        this.coinnessScrapService = coinnessScrapService;
    }

    @Scheduled(fixedDelay = 60000) // 1분마다 실행
    public void scrapCoinnessBreakingNews() {
        try {
            coinnessScrapService.scrapCoinnessBreakingNews();
        } catch (Exception e) {
            log.error("코인니스 속보 스케줄 스크래핑 중 오류 발생", e);
        }
    }

    @Scheduled(fixedDelay = 60000) // 1분마다 실행
    public void scrapCoinnessArticles() {
        try {
            coinnessScrapService.scrapCoinnessArticles();
        } catch (Exception e) {
            log.error("코인니스 기사 스케줄 스크래핑 중 오류 발생", e);
        }
    }
}
