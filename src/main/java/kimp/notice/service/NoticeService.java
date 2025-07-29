package kimp.notice.service;

import kimp.common.dto.PageRequestDto;
import kimp.notice.dto.notice.ExchangeNoticeDto;
import kimp.notice.dto.notice.NoticeDto;
import kimp.market.Enum.MarketType;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeService {

    public NoticeDto getNoticeById(Long id);

    public NoticeDto getNoticeByLink(String link);

    public ExchangeNoticeDto<Page<NoticeDto>> getAllNotices(PageRequestDto pageRequestDto);

    public NoticeDto createNotice(String title, String link, LocalDateTime date);

    public NoticeDto findNoticeByLink(String link);

    public NoticeDto updateNotice(Long id, String title, String link);

    public NoticeDto updateNoticeDate(Long id, LocalDateTime date);

    /**
     * 특정 거래소의 최신 공지사항 링크들을 가져옴 (초기화 실패 시 백업용)
     * @param marketType 거래소 타입
     * @param limit 가져올 최대 개수
     * @return 공지사항 링크 목록
     */
    public List<String> getRecentNoticeLinks(MarketType marketType, int limit);
}
