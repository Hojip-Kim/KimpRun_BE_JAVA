package kimp.notice.service.impl;

import kimp.common.dto.request.PageRequestDto;
import kimp.common.method.DtoConverter;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.notice.dao.NoticeDao;
import kimp.notice.dto.response.ExchangeNoticeDto;
import kimp.notice.dto.response.NoticeDto;
import kimp.notice.entity.Notice;
import kimp.notice.service.NoticeService;
import kimp.market.Enum.MarketType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService
{

    private final NoticeDao noticeDao;
    private final DtoConverter dtoConverter;

    public NoticeServiceImpl(NoticeDao noticeDao, DtoConverter dtoConverter) {
        this.noticeDao = noticeDao;
        this.dtoConverter = dtoConverter;
    }

    @Override
    public NoticeDto getNoticeByLink(String link) {
        Notice notice = noticeDao.getNoticeByLink(link);
        return dtoConverter.convertNoticeToDto(notice);
    }

    @Override
    @Transactional
    public ExchangeNoticeDto<Page<NoticeDto>> getAllNotices(kimp.notice.vo.GetNoticeByExchangeVo vo) {
        Pageable pageable = PageRequest.of(vo.getPageRequestDto().getPage(), vo.getPageRequestDto().getSize());

        Page<Notice> noticePage = this.noticeDao.findAllByOrderByRegistedAtAsc(pageable);

        if(noticePage.isEmpty()){
            throw new KimprunException(KimprunExceptionEnum.REQUEST_ACCEPTED, "Not have data", HttpStatus.ACCEPTED, "hello");
        }
        Page<NoticeDto> pageNoticeDto = dtoConverter.convertNoticePageToDtoPage(noticePage);

        return dtoConverter.wrappingDtosToExchangeNoticeDto(MarketType.ALL, pageNoticeDto);
    }

    @Override
    @Transactional
    public NoticeDto updateNoticeDate(Long id, LocalDateTime date) {
        Notice notice = noticeDao.getNotice(id);
        notice.updateDate(date);
        return dtoConverter.convertNoticeToDto(notice);
    }

    @Override
    public List<String> getRecentNoticeLinks(MarketType marketType, int limit) {
        try {
            return noticeDao.getRecentNoticeLinks(marketType, limit);
        } catch (Exception e) {
            log.error("최신 공지사항 링크 조회 실패: {} - {}", marketType, e.getMessage());
            return List.of(); // 빈 리스트 반환
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeDto> getNoticesAfterDate(MarketType marketType, LocalDateTime afterDate) {
        try {
            if (afterDate == null) {
                return List.of();
            }
            List<Notice> notices = noticeDao.getNoticesAfterDate(marketType, afterDate);
            return notices.stream()
                .map(notice -> dtoConverter.convertNoticeToDto(notice))
                .toList();
        } catch (Exception e) {
            log.error("날짜 이후 공지사항 조회 실패: {} - {}", marketType, e.getMessage());
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeDto> getAllNoticesByMarketType(MarketType marketType) {
        try {
            List<Notice> notices = noticeDao.findAllNoticesByMarketType(marketType);
            return notices.stream()
                .map(notice -> dtoConverter.convertNoticeToDto(notice))
                .toList();
        } catch (Exception e) {
            log.error("거래소별 모든 공지사항 조회 실패: {} - {}", marketType, e.getMessage());
            return List.of();
        }
    }
}
