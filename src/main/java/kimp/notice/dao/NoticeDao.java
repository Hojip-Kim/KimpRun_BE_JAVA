package kimp.notice.dao;

import kimp.notice.entity.Notice;
import kimp.market.Enum.MarketType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
}
