package kimp.notice.dao.impl;

import kimp.notice.dao.NoticeDao;
import kimp.notice.entity.Notice;
import kimp.notice.repository.NoticeRepository;
import kimp.market.Enum.MarketType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class NoticeDaoImpl implements NoticeDao {
    private final NoticeRepository noticeRepository;

    public NoticeDaoImpl(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Override
    public Notice createNotice(Notice notice) {

        return this.noticeRepository.save(notice);

    }

    @Override
    @Transactional
    public boolean createBulkNotice(List<Notice> notices) {
        List<Notice> savedNotice = this.noticeRepository.saveAll(notices);

        return savedNotice.size() == notices.size();
    }

    @Override
    public Notice getNotice(long id) {
        Optional<Notice> notice = this.noticeRepository.findById(id);

        if(notice.isEmpty()) {
            throw new IllegalArgumentException("not found notice id : " + id);
        }

        return notice.get();
    }

    @Override
    public Notice getNoticeByLink(String link) {
        Notice notice = this.noticeRepository.findNoticeByLink(link);

        return notice;
    }

    @Override
    public void deleteNotice(long id) {
        this.getNotice(id);
        this.noticeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Page<Notice> findByExchangeIdOrderByRegistedAtAsc(long exchangeId, Pageable pageable) {
        return this.noticeRepository.findByExchangeIdOrderByDateDesc(exchangeId, pageable);
    }

    @Override
    public Page<Notice> findAllByOrderByRegistedAtAsc(Pageable pageable) {
        return this.noticeRepository.findAllByOrderByDateDescWithFetch(pageable);
    }

    @Override
    public List<String> getRecentNoticeLinks(MarketType marketType, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return this.noticeRepository.findRecentNoticeLinksByMarketType(marketType, pageable);
    }

    @Override
    public LocalDateTime getLatestNoticeDate(MarketType marketType) {
        return this.noticeRepository.findLatestNoticeDateByMarketType(marketType);
    }

    @Override
    public List<String> getNoticeLinksAfterDate(MarketType marketType, LocalDateTime afterDate) {
        return this.noticeRepository.findNoticeLinksAfterDate(marketType, afterDate);
    }

    @Override
    public List<Notice> findAllNoticesByMarketType(MarketType marketType) {
        return this.noticeRepository.findAllNoticesByMarketTypeWithFetch(marketType);
    }
}
