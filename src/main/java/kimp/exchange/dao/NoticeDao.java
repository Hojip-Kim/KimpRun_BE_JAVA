package kimp.exchange.dao;

import kimp.exchange.entity.Notice;
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


}
