package kimp.scrap.component.impl.exchange;

import kimp.scrap.dto.coinone.CoinoneNoticeDto;
import kimp.scrap.dto.python.PythonNoticeResponseDto;
import kimp.notice.dto.notice.NoticeParsedData;
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
public class CoinoneScrap extends ExchangeScrapAbstract<CoinoneNoticeDto> {

    @Value("${coinone.notice.url}")
    private String noticeUrl;

    @Value("${coinone.notice.detail.url}")
    private String coinoneNoticeDetailUrl;

    @Value("${coinone.origin}")
    private String coinoneOrigin;

    @Value("${coinone.referer}")
    private String coinoneReferer;

    private List<NoticeParsedData> parsedData = new ArrayList<>();
    private List<NoticeParsedData> newNotices = new ArrayList<>();

    public CoinoneScrap(RestClient restClient, StringRedisTemplate stringRedisTemplate) {
        super(restClient, stringRedisTemplate, CoinoneNoticeDto.class);
    }

    /**
     * Flask 서비스 응답을 기존 CoinoneNoticeDto 형태로 변환
     */
    @Override
    protected CoinoneNoticeDto convertPythonResponse(PythonNoticeResponseDto pythonResponse) {
        log.debug("Python 응답을 CoinoneNoticeDto로 변환: {} 개 공지사항", 
                pythonResponse.getResults() != null ? pythonResponse.getResults().size() : 0);
        
        // parseNoticeData에서 Python 응답을 직접 사용하므로 null 반환
        return null;
    }

    @Override
    public List<NoticeParsedData> getFieldNewNotice() {
        return this.newNotices;
    }



    @Override
    public void setNewNotice(List<NoticeParsedData> notice) {
        this.newNotices.clear();
        this.newNotices = notice;
    }

    @Override
    public void setNewParsedData(List<NoticeParsedData> newParsedData) {
        this.parsedData.clear();
        this.parsedData = newParsedData;
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "*/*");
        headers.set("referer", coinoneReferer);
        headers.set("origin", coinoneOrigin);
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
     * Flask 서비스를 통해 공지사항 데이터를 파싱
     */
    @Override
    public List<NoticeParsedData> parseNoticeData() throws IOException {
        try {
            log.info("Coinone Python 서비스를 통한 공지사항 파싱 시작");
            
            // Flask 서비스에서 공지사항 가져오기
            PythonNoticeResponseDto pythonResponse = super.fetchPythonNotices();
            
            // Flask 응답을 NoticeParsedData로 변환
            List<NoticeParsedData> noticeParsedDataList = super.convertPythonToNoticeParsedData(pythonResponse);
            
            if (noticeParsedDataList.isEmpty()) {
                log.warn("Coinone Python 서비스에서 공지사항을 가져오지 못했습니다");
                return new ArrayList<>();
            }
            
            log.info("Coinone 공지사항 {} 개 파싱 완료", noticeParsedDataList.size());
            return noticeParsedDataList;
            
        } catch (Exception e) {
            log.error("Coinone 공지사항 파싱 실패: {}", e.getMessage(), e);
            throw new IOException("Coinone 공지사항 파싱 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public String getAbsoluteUrl() {
        return this.coinoneNoticeDetailUrl;
    }

    @Override
    public MarketType getMarketType() {
        return MarketType.COINONE;
    }
}
