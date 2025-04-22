package kimp.exchange.service;

import kimp.exchange.dto.notice.NoticeDto;

import java.time.LocalDateTime;

public interface NoticeService {

    public NoticeDto getNoticeById(Long id);

    public NoticeDto createNotice(String title, String link, LocalDateTime date);

    public NoticeDto findNoticeByLink(String link);

    public NoticeDto updateNotice(Long id, String title, String link);

    public NoticeDto updateNoticeDate(Long id, LocalDateTime date);

}
