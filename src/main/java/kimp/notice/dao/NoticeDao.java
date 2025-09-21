package kimp.notice.dao;

import kimp.notice.entity.Notice;
import kimp.market.Enum.MarketType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeDao {

    public Notice createNotice(Notice notice);

    public boolean createBulkNotice(List<Notice> notices);

    public Notice getNotice(long id);

    public Notice getNoticeByLink(String link);

    public void deleteNotice(long id);

    public Page<Notice> findByExchangeIdOrderByRegistedAtAsc(long exchangeId, Pageable pageable);

    public Page<Notice> findAllByOrderByRegistedAtAsc(Pageable pageable);

    /**
     * 특정 거래소의 최신 공지사항 링크들을 가져옴
     * @param marketType 거래소 타입
     * @param limit 가져올 최대 개수
     * @return 공지사항 링크 목록
     */
    public List<String> getRecentNoticeLinks(MarketType marketType, int limit);

    /**
     * 특정 거래소의 가장 최근 공지사항 날짜를 가져옴
     * @param marketType 거래소 타입
     * @return 가장 최근 공지사항 날짜 (없으면 null)
     */
    public LocalDateTime getLatestNoticeDate(MarketType marketType);

    /**
     * 특정 거래소의 지정된 날짜 이후 새로운 공지사항들의 링크를 가져옴
     * @param marketType 거래소 타입
     * @param afterDate 기준 날짜 (이 날짜보다 최신인 공지사항들을 가져옴)
     * @return 새로운 공지사항 링크 목록
     */
    public List<String> getNoticeLinksAfterDate(MarketType marketType, LocalDateTime afterDate);

    /**
     * 특정 거래소의 모든 공지사항을 가져옴 (새로운 로직용)
     * @param marketType 거래소 타입
     * @return 해당 거래소의 모든 공지사항 목록
     */
    public List<Notice> findAllNoticesByMarketType(MarketType marketType);

    /**
     * N+1 문제 해결: 여러 링크에 대한 기존 공지사항 존재 여부를 한번에 확인
     * @param links 확인할 링크 목록
     * @return 이미 존재하는 링크 목록
     */
    public List<String> findExistingNoticeLinks(List<String> links);

    /**
     * JPA 배치 최적화된 대량 공지사항 생성
     * Hibernate batch_size 설정을 활용한 배치 INSERT
     * @param notices 생성할 공지사항 목록
     * @return 생성 성공 여부
     */
    public boolean createBulkNoticeOptimized(List<Notice> notices);
}
