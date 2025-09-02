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

    /**
     * 특정 거래소의 가장 최근 공지사항 날짜를 가져옴
     * @param marketType 거래소 타입
     * @return 가장 최근 공지사항 날짜 (없으면 null)
     */
    public LocalDateTime getLatestNoticeDate(MarketType marketType);

    /**
     * 특정 거래소의 지정된 날짜 이후 새로운 공지사항들의 링크를 가져옴
     * @param marketType 거래소 타입
     * @param afterDate 기준 날짜
     * @return 새로운 공지사항 링크 목록
     */
    public List<String> getNoticeLinksAfterDate(MarketType marketType, LocalDateTime afterDate);

    /**
     * 특정 거래소의 모든 공지사항을 가져옴 (새로운 로직용)
     * @param marketType 거래소 타입
     * @return 해당 거래소의 모든 공지사항 DTO 목록
     */
    public List<NoticeDto> getAllNoticesByMarketType(MarketType marketType);
}
