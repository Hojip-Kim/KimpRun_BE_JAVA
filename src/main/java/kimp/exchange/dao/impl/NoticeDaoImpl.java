package kimp.exchange.dao.impl;

import kimp.exchange.dao.NoticeDao;
import kimp.exchange.entity.Notice;
import kimp.exchange.repository.NoticeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<Notice> findByExchangeOrderByRegistedAtDesc(long exchangeId, Pageable pageable) {
        return this.noticeRepository.findByExchangeIdOrderByRegistedAtAsc(exchangeId, pageable);
    }
}
