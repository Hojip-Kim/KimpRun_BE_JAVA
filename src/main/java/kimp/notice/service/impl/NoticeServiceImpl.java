package kimp.notice.service.impl;

import kimp.common.dto.PageRequestDto;
import kimp.common.method.DtoConverter;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.notice.dao.NoticeDao;
import kimp.notice.dto.notice.ExchangeNoticeDto;
import kimp.notice.dto.notice.NoticeDto;
import kimp.notice.entity.Notice;
import kimp.notice.service.NoticeService;
import kimp.market.Enum.MarketType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    public NoticeDto getNoticeByLink(String link) {
        Notice notice = noticeDao.getNoticeByLink(link);
        return dtoConverter.convertNoticeToDto(notice);
    }

    @Override
    @Transactional
    public ExchangeNoticeDto<Page<NoticeDto>> getAllNotices(PageRequestDto pageRequestDto) {
        Pageable pageable = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize());

        Page<Notice> noticePage = this.noticeDao.findAllByOrderByRegistedAtAsc(pageable);

        if(noticePage.isEmpty()){
            throw new KimprunException(KimprunExceptionEnum.REQUEST_ACCEPTED, "Not have data", HttpStatus.ACCEPTED, "hello");
        }
        Page<NoticeDto> pageNoticeDto = dtoConverter.convertNoticePageToDtoPage(noticePage);

        return dtoConverter.wrappingDtosToExchangeNoticeDto(MarketType.ALL, pageNoticeDto);
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
