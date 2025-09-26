package kimp.exchange.service.impl;

import kimp.scrap.dto.python.PythonNoticeResponseDto;
import kimp.exchange.service.PythonScrapService;
import kimp.notice.dto.notice.NoticeParsedData;
import kimp.market.Enum.MarketType;
import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Python 스크래핑 서비스 호출 구현체
 */
@Service
@Slf4j
public class PythonScrapServiceImpl implements PythonScrapService {
    
    private final RestClient restClient;
    
    @Value("${notice.server.url:http://localhost:8090}")
    private String pythonServiceBaseUrl;
    
    // 날짜 포맷터들 (Python 서비스에서 YYYY.MM.DD 형식으로 반환)
    private static final DateTimeFormatter PYTHON_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter[] FALLBACK_FORMATS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };
    
    public PythonScrapServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }
    
    @Override
    public List<NoticeParsedData> getNoticesByExchange(MarketType marketType) {
        String exchangeName = convertMarketTypeToExchangeName(marketType);
        String url = pythonServiceBaseUrl + "/notices/" + exchangeName;
        
        try {

            PythonNoticeResponseDto responseBody = restClient.get()
                .uri(url)
                .retrieve()
                .body(PythonNoticeResponseDto.class);
            
            if (responseBody != null && responseBody.isSuccess()) {
                return convertToNoticeParsedDataList(responseBody.getResults(), marketType);
            } else {
                String error = responseBody != null ? responseBody.getError() : "null response";
                log.error("Python 서비스 오류: {}", error);
                return new ArrayList<>();
            }
            
        } catch (Exception e) {
            log.error("Python 스크래핑 서비스 호출 실패: {} - {}", url, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<NoticeParsedData> getAllNotices() {
        List<NoticeParsedData> allNotices = new ArrayList<>();
        
        // 모든 거래소에 대해 순차적으로 호출
        for (MarketType marketType : getActiveMarketTypes()) {
            List<NoticeParsedData> notices = getNoticesByExchange(marketType);
            allNotices.addAll(notices);
        }
        
        return allNotices;
    }
    
    @Override
    public boolean isServiceHealthy() {
        String url = pythonServiceBaseUrl + "/";
        
        try {
            String response = restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
            
            return response != null;
            
        } catch (Exception e) {
            log.error("Python 서비스 헬스체크 실패: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * MarketType을 Python 서비스에서 사용하는 거래소 이름으로 변환
     */
    private String convertMarketTypeToExchangeName(MarketType marketType) {
        switch (marketType) {
            case UPBIT:
                return "upbit";
            case BITHUMB:
                return "bithumb";
            case BINANCE:
                return "binance";
            case COINONE:
                return "coinone";
            default:
                throw new KimprunException(KimprunExceptionEnum.PYTHON_SERVICE_EXCEPTION, "Unsupported exchange type: " + marketType, HttpStatus.BAD_REQUEST, "PythonScrapServiceImpl.convertMarketTypeToExchangeName");
        }
    }
    
    /**
     * 활성화된 거래소 타입 목록 반환
     */
    private MarketType[] getActiveMarketTypes() {
        return new MarketType[]{
            MarketType.UPBIT,
            MarketType.BITHUMB,
            MarketType.BINANCE,
            MarketType.COINONE
        };
    }
    
    /**
     * Python 서비스 응답을 기존 NoticeParsedData 구조로 변환
     */
    private List<NoticeParsedData> convertToNoticeParsedDataList(
            List<PythonNoticeResponseDto.PythonNoticeDto> pythonNotices, 
            MarketType marketType) {
        
        List<NoticeParsedData> result = new ArrayList<>();
        
        if (pythonNotices == null) {
            return result;
        }
        
        for (PythonNoticeResponseDto.PythonNoticeDto pythonNotice : pythonNotices) {
            try {
                String title = pythonNotice.getTitle();
                String absoluteLink = buildAbsoluteLink(pythonNotice.getLink(), marketType);
                LocalDateTime parsedDate = parseDate(pythonNotice.getDate());
                
                NoticeParsedData parsedData = new NoticeParsedData(title, absoluteLink, parsedDate);
                result.add(parsedData);
                
            } catch (Exception e) {
                log.warn("공지사항 변환 실패: {} - {}", pythonNotice.getTitle(), e.getMessage());
            }
        }
        
        return result;
    }
    
    /**
     * 상대 경로를 절대 경로로 변환
     */
    private String buildAbsoluteLink(String relativeLink, MarketType marketType) {
        if (relativeLink == null || relativeLink.startsWith("http")) {
            return relativeLink; // 이미 절대 경로
        }
        
        String baseUrl;
        switch (marketType) {
            case UPBIT:
                baseUrl = "https://upbit.com";
                break;
            case BITHUMB:
                baseUrl = "https://feed.bithumb.com";
                break;
            case BINANCE:
                baseUrl = "https://www.binance.com";
                break;
            case COINONE:
                baseUrl = "https://coinone.co.kr";
                break;
            default:
                return relativeLink;
        }
        
        return baseUrl + relativeLink;
    }
    
    /**
     * 날짜 문자열을 LocalDateTime으로 파싱
     */
    private LocalDateTime parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return LocalDateTime.now();
        }
        
        // 먼저 기본 포맷 시도
        try {
            return LocalDateTime.parse(dateString + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            for (DateTimeFormatter format : FALLBACK_FORMATS) {
                try {
                    return LocalDateTime.parse(dateString + " 00:00:00",
                        DateTimeFormatter.ofPattern(format.toString() + " HH:mm:ss"));
                } catch (DateTimeParseException ignored) {
                }
            }
        }
        return LocalDateTime.now();
    }
} 