package kimp.exchange.service.impl;

import kimp.common.method.DtoConverter;
import kimp.exchange.dao.NoticeDao;
import kimp.exchange.dto.notice.NoticeDto;
import kimp.exchange.entity.Notice;
import kimp.exchange.service.NoticeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeDao noticeDao;
    private final DtoConverter dtoConverter;

    public NoticeServiceImpl(NoticeDao noticeDao, DtoConverter dtoConverter) {
        this.noticeDao = noticeDao;
        this.dtoConverter = dtoConverter;
    }

    @Override
    public NoticeDto getNoticeById(Long id) {
        Notice notice = noticeDao.getNotice(id);
        return dtoConverter.convertNoticeToDto(notice);
    }

    @Override
    public NoticeDto createNotice(String title, String link, LocalDateTime date) {
        Notice notice = new Notice(title, link, date);

        Notice savedNotice = noticeDao.createNotice(notice);

        return dtoConverter.convertNoticeToDto(savedNotice);
    }

    @Override
    @Transactional
    public NoticeDto findNoticeByLink(String link) {
        Notice notice = this.noticeDao.getNoticeByLink(link);

        return dtoConverter.convertNoticeToDto(notice);
    }

    @Override
    @Transactional
    public NoticeDto updateNotice(Long id, String title, String link) {
        Notice notice = noticeDao.getNotice(id);
        notice.updateTitle(title);
        notice.updateLink(link);

        return dtoConverter.convertNoticeToDto(notice);
    }

    @Override
    @Transactional
    public NoticeDto updateNoticeDate(Long id, LocalDateTime date) {
        Notice notice = noticeDao.getNotice(id);
        notice.updateDate(date);

        return dtoConverter.convertNoticeToDto(notice);
    }
}
