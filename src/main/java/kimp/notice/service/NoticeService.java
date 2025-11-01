package kimp.notice.service;

import kimp.common.dto.request.PageRequestDto;
import kimp.notice.dto.response.ExchangeNoticeDto;
import kimp.notice.dto.response.NoticeDto;
import kimp.market.Enum.MarketType;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeService {

    public NoticeDto getNoticeByLink(String link);

    public ExchangeNoticeDto<Page<NoticeDto>> getAllNotices(kimp.notice.vo.GetNoticeByExchangeVo vo);

    public NoticeDto updateNoticeDate(Long id, LocalDateTime date);

    /**
     * 특정 거래소의 최신 공지사항 링크들을 가져옴 (초기화 실패 시 백업용)
     * @param marketType 거래소 타입
     * @param limit 가져올 최대 개수
     * @return 공지사항 링크 목록
     */
    public List<String> getRecentNoticeLinks(MarketType marketType, int limit);

    /**
     * 특정 거래소의 지정된 날짜 이후 새로운 공지사항들을 가져옴 (URL과 날짜 포함)
     * Redis 캐시 초기화 시 사용
     * @param marketType 거래소 타입
     * @param afterDate 기준 날짜
     * @return 새로운 공지사항 DTO 목록
     */
    public List<NoticeDto> getNoticesAfterDate(MarketType marketType, LocalDateTime afterDate);

    /**
     * 특정 거래소의 모든 공지사항을 가져옴 (새로운 로직용)
     * @param marketType 거래소 타입
     * @return 해당 거래소의 모든 공지사항 DTO 목록
     */
    public List<NoticeDto> getAllNoticesByMarketType(MarketType marketType);
}
