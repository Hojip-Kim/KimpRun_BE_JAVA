package kimp.exchange.service;

import kimp.common.dto.PageRequestDto;
import kimp.exchange.dto.notice.ExchangeNoticeDto;
import kimp.exchange.dto.notice.NoticeDto;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface NoticeService {

    public NoticeDto getNoticeById(Long id);

    public NoticeDto getNoticeByLink(String link);

    public ExchangeNoticeDto<Page<NoticeDto>> getAllNotices(PageRequestDto pageRequestDto);

    public NoticeDto createNotice(String title, String link, LocalDateTime date);

    public NoticeDto findNoticeByLink(String link);

    public NoticeDto updateNotice(Long id, String title, String link);

    public NoticeDto updateNoticeDate(Long id, LocalDateTime date);

}
