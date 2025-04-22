package kimp.exchange.repository;

import kimp.exchange.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {


    public Page<Notice> findByExchangeIdOrderByRegistedAtAsc(long exchangeId, Pageable pageable);

    public Notice findNoticeByLink(String link);

}
