package kimp.exchange.component.impl.exchange;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.exchange.component.ExchangeScrap;
import kimp.exchange.dto.python.PythonNoticeResponseDto;
import kimp.notice.dto.notice.NoticeParsedData;
import kimp.notice.dto.notice.NoticeResponseDto;
import kimp.market.Enum.MarketType;
import kimp.util.MumurHashUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
public abstract class ExchangeScrapAbstract<T> implements ExchangeScrap<T> {

    private final RestClient restClient;
    private final StringRedisTemplate redisTemplate;
    private final Class<T> responseType;

    @Value("${notice.server.url:http://localhost:8090}")
    private String pythonServiceUrl;

    // 날짜 파싱을 위한 포맷터들
    private static final DateTimeFormatter PYTHON_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter[] FALLBACK_FORMATS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };

    public ExchangeScrapAbstract(RestClient restClient, StringRedisTemplate redisTemplate, Class<T> responseType) {
        this.restClient = restClient;
        this.redisTemplate = redisTemplate;
        this.responseType = responseType;
    }

    /**
     * Python 스크래핑 서비스에서 공지사항을 가져옴
     */
    @Override
    public T getNoticeFromAPI() throws IOException {
        try {
            // Python 서비스 엔드포인트 구성
            String exchangeName = getMarketType().name().toLowerCase();
            String url = pythonServiceUrl + "/notices/" + exchangeName;
            
            log.info("Python 서비스 호출: {}", url);
            
            // Python 서비스 호출 (RestClient 사용)
            PythonNoticeResponseDto responseBody = restClient.get()
                .uri(url)
                .retrieve()
                .body(PythonNoticeResponseDto.class);
            
            if (responseBody != null && responseBody.isSuccess()) {
                // Python 응답을 기존 타입으로 변환 (각 하위 클래스에서 구현)
                return convertPythonResponse(responseBody);
            } else {
                String error = responseBody != null ? responseBody.getError() : "null response";
                throw new KimprunException(
                    KimprunExceptionEnum.INTERNAL_SERVER_ERROR, 
                    "Python 서비스 오류: " + error, 
                    HttpStatus.INTERNAL_SERVER_ERROR, 
                    "trace"
                );
            }
            
        } catch (Exception e) {
            log.error("Python 서비스 호출 실패: {}", e.getMessage());
            throw new KimprunException(
                KimprunExceptionEnum.INTERNAL_SERVER_ERROR, 
                "Python 서비스 호출 실패: " + e.getMessage(), 
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "trace"
            );
        }
    }

    /**
     * Python 서비스 응답을 기존 DTO 타입으로 변환 (추상 메서드)
     */
    protected abstract T convertPythonResponse(PythonNoticeResponseDto pythonResponse);

    /**
     * Python 응답을 NoticeParsedData 리스트로 변환하는 공통 메서드
     */
    protected List<NoticeParsedData> convertPythonToNoticeParsedData(PythonNoticeResponseDto pythonResponse) {
        List<NoticeParsedData> result = new ArrayList<>();
        
        if (pythonResponse.getResults() == null) {
            return result;
        }
        
        for (PythonNoticeResponseDto.PythonNoticeDto pythonNotice : pythonResponse.getResults()) {
            try {
                String title = pythonNotice.getTitle();
                String absoluteLink = buildAbsoluteLink(pythonNotice.getLink());
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
     * RestClient를 사용하여 Python 서비스에서 공지사항을 직접 가져오는 메서드
     */
    protected PythonNoticeResponseDto fetchPythonNotices() throws IOException {
        try {
            String exchangeName = getMarketType().name().toLowerCase();
            String url = pythonServiceUrl + "/notices/" + exchangeName;
            
            log.debug("Python 서비스 호출: {}", url);
            
            PythonNoticeResponseDto response = restClient.get()
                .uri(url)
                .retrieve()
                .body(PythonNoticeResponseDto.class);
                
            if (response == null || !response.isSuccess()) {
                String error = response != null ? response.getError() : "null response";
                throw new IOException("Python 서비스 오류: " + error);
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("Python 서비스 호출 실패: {}", e.getMessage());
            throw new IOException("Python 서비스 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 상대 경로를 절대 경로로 변환
     */
    private String buildAbsoluteLink(String relativeLink) {
        if (relativeLink == null || relativeLink.startsWith("http")) {
            return relativeLink; // 이미 절대 경로
        }
        
        return getAbsoluteUrl() + relativeLink;
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
            // 폴백 포맷들 시도
            for (DateTimeFormatter format : FALLBACK_FORMATS) {
                try {
                    return LocalDateTime.parse(dateString + " 00:00:00",
                        DateTimeFormatter.ofPattern(format.toString() + " HH:mm:ss"));
                } catch (DateTimeParseException ignored) {
                    // 다음 포맷 시도
                }
            }
        }
        
        log.warn("날짜 파싱 실패, 현재 시간 사용: {}", dateString);
        return LocalDateTime.now();
    }

    protected StringRedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }

    protected RestClient getRestClient() {
        return this.restClient;
    }

    @Override
    public boolean isUpdatedNotice(String savedRedisHashCode, List<NoticeParsedData> recentNoticeDataList){
        StringBuffer sb = new StringBuffer();
        for(NoticeParsedData recentNoticeData : recentNoticeDataList){
            sb.append(recentNoticeData.getTitle());
        }
        String recentHashCode = MumurHashUtil.stringTo128bitHashCode(sb.toString());

        return !recentHashCode.equals(savedRedisHashCode);
    }

    @Override
    public NoticeResponseDto convertNoticeDataToDto() throws IOException {
        return new NoticeResponseDto(getMarketType(),getAbsoluteUrl(), getNoticeData());
    }

    public Class<T> getResponseType(){
        return responseType;
    };

    @Override
    public abstract List<NoticeParsedData> getFieldNewNotice();

    @Override
    public abstract void setNoticeToRedis(List<NoticeParsedData> noticeParsedDataList);

    @Override
    public abstract String getNoticeFromRedis() throws IOException;

    @Override
    public abstract List<NoticeParsedData> getNewNotice(List<NoticeParsedData> recentNoticeDataList);

    @Override
    public abstract void setNewNotice(List<NoticeParsedData> notice);

    @Override
    public abstract void setNewParsedData(List<NoticeParsedData> newParsedData);

    @Override
    public abstract HttpHeaders getHeaders();

    @Override
    public abstract HttpMethod getMethod();

    @Override
    public abstract List<NoticeParsedData> getNoticeData();

    @Override
    public abstract String getNoticeUrl();

    @Override
    public abstract boolean isNecessityOfDetour();

    @Override
    public abstract List<NoticeParsedData> parseNoticeData() throws IOException;

    @Override
    public abstract String getAbsoluteUrl();

    @Override
    public abstract MarketType getMarketType();
}
