package kimp.scrap.component.impl.exchange;

import kimp.notice.dto.response.NoticeParsedData;
import kimp.scrap.dto.internal.python.PythonNoticeResponseDto;
import kimp.scrap.dto.internal.upbit.UpbitNoticeDto;
import kimp.market.Enum.MarketType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class UpbitScrap extends ExchangeScrapAbstract<UpbitNoticeDto> {

    @Value("${upbit.notice.url}")
    private String noticeUrl;

    @Value("${upbit.notice.detail.url}")
    private String upbitNoticeDetailUrl;

    private List<NoticeParsedData> parsedData = new ArrayList<>();
    private List<NoticeParsedData> newNotices = new ArrayList<>();

    public UpbitScrap(RestClient restClient, StringRedisTemplate stringRedisTemplate) {
        super(restClient, stringRedisTemplate, UpbitNoticeDto.class);
    }

    /**
     * Python 서비스 응답을 기존 UpbitNoticeDto 형태로 변환
     */
    @Override
    protected UpbitNoticeDto convertPythonResponse(PythonNoticeResponseDto pythonResponse) {
        log.debug("Python 응답을 UpbitNoticeDto로 변환: {} 개 공지사항", 
                pythonResponse.getResults() != null ? pythonResponse.getResults().size() : 0);
        
        // 여기서는 실제 UpbitNoticeDto를 생성하지 않고 null 반환
        // parseNoticeData에서 Python 응답을 직접 사용
        return null;
    }

    @Override
    public List<NoticeParsedData> getFieldNewNotice() {
        return this.newNotices;
    }


    @Override
    public void setNewNotice(List<NoticeParsedData> notice) {
        this.newNotices.clear();
        if(notice != null && !notice.isEmpty()){
            this.newNotices.addAll(notice);
        }
    }

    @Override
    public void setNewParsedData(List<NoticeParsedData> newParsedData) {
        this.parsedData.clear();
        if(newParsedData != null && !newParsedData.isEmpty()){
            this.parsedData.addAll(newParsedData);
        }
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.setContentType(new MediaType("application", "json"));
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public List<NoticeParsedData> getNoticeData() {
        return this.parsedData;
    }

    @Override
    public String getNoticeUrl() {
        return this.noticeUrl;
    }

    @Override
    public boolean isNecessityOfDetour() {
        return false;
    }

    /**
     * Python 서비스를 통해 공지사항 데이터를 파싱
     */
    @Override
    public List<NoticeParsedData> parseNoticeData() throws IOException {
        try {

            // Python 서비스에서 공지사항 가져오기 (부모 클래스의 메서드 사용)
            PythonNoticeResponseDto pythonResponse = super.fetchPythonNotices();
            
            // Python 응답을 NoticeParsedData로 변환
            List<NoticeParsedData> noticeParsedDataList = super.convertPythonToNoticeParsedData(pythonResponse);
            
            if (noticeParsedDataList.isEmpty()) {
                log.warn("Upbit Python 서비스에서 공지사항을 가져오지 못했습니다");
                return new ArrayList<>();
            }
            
            return noticeParsedDataList;
            
        } catch (Exception e) {
            log.error("Upbit 공지사항 파싱 실패: {}", e.getMessage(), e);
            throw new IOException("Upbit 공지사항 파싱 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public String getAbsoluteUrl() {
        return this.upbitNoticeDetailUrl;
    }

    @Override
    public MarketType getMarketType() {
        return MarketType.UPBIT;
    }
}
