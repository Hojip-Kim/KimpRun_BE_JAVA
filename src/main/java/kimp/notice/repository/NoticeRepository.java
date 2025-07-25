package kimp.notice.repository;

import kimp.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoticeRepository extends JpaRepository<Notice, Long> {


    public Page<Notice> findByExchangeIdOrderByDateDesc(long exchangeId, Pageable pageable);

    public Page<Notice> findByOrderByDateDesc(Pageable pageable);

    @Query("SELECT n FROM Notice n JOIN FETCH n.exchange WHERE n.link = :link")
    public Notice findNoticeByLink(String link);

}
