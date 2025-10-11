package kimp.exchange.service;

import kimp.notice.dto.response.NoticeParsedData;
import kimp.market.Enum.MarketType;

import java.util.List;

/**
 * Python Notice Scraping Service Interface
 * Python으로 구현된 외부 스크래핑 서비스를 호출하는 인터페이스
 */
public interface PythonScrapService {
    
    /**
     * 특정 거래소의 공지사항을 Python 서비스에서 가져옴
     * @param marketType 거래소 타입 (UPBIT, BITHUMB, BINANCE, COINONE)
     * @return 파싱된 공지사항 데이터 리스트
     */
    List<NoticeParsedData> getNoticesByExchange(MarketType marketType);
    
    /**
     * 모든 거래소의 공지사항을 Python 서비스에서 일괄 가져옴
     * @return 거래소별 공지사항 데이터 맵
     */
    List<NoticeParsedData> getAllNotices();
    
    /**
     * Python 스크래핑 서비스의 상태를 확인
     * @return 서비스 상태 (true: 정상, false: 오류)
     */
    boolean isServiceHealthy();
} 