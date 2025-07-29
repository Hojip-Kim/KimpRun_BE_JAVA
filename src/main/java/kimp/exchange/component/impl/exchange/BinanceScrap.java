package kimp.exchange.component.impl.exchange;

import kimp.exchange.dto.binance.BinanceNoticeDto;
import kimp.exchange.dto.python.PythonNoticeResponseDto;
import kimp.notice.dto.notice.NoticeParsedData;
import kimp.market.Enum.MarketType;
import kimp.util.MumurHashUtil;
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
public class BinanceScrap extends ExchangeScrapAbstract<BinanceNoticeDto> {

    @Value("${binance.notice.url}")
    private String noticeUrl;

    @Value("${binance.notice.detail.url}")
    private String noticeDetailAbsoluteUrl;

    private List<NoticeParsedData> parsedData = new ArrayList<>();
    private List<NoticeParsedData> newNotices = new ArrayList<>();

    public BinanceScrap(RestClient restClient, StringRedisTemplate stringRedisTemplate) {
        super(restClient, stringRedisTemplate, BinanceNoticeDto.class);
    }

    /**
     * Python 서비스 응답을 기존 BinanceNoticeDto 형태로 변환
     */
    @Override
    protected BinanceNoticeDto convertPythonResponse(PythonNoticeResponseDto pythonResponse) {
        log.debug("Python 응답을 BinanceNoticeDto로 변환: {} 개 공지사항", 
                pythonResponse.getResults() != null ? pythonResponse.getResults().size() : 0);
        
        // parseNoticeData에서 Python 응답을 직접 사용하므로 null 반환
        return null;
    }

    @Override
    public List<NoticeParsedData> getFieldNewNotice() {
        return this.newNotices;
    }

    @Override
    public void setNoticeToRedis(List<NoticeParsedData> noticeParsedDataList) {
        StringBuilder sb = new StringBuilder();

        for(NoticeParsedData noticeParsedData : noticeParsedDataList) {
            sb.append(noticeParsedData.getTitle());
        }

        String noticeHashCode = MumurHashUtil.stringTo128bitHashCode(sb.toString());
        super.getRedisTemplate().opsForValue().set(MarketType.BINANCE.getMarketName(), noticeHashCode);
    }

    @Override
    public String getNoticeFromRedis() {
        StringRedisTemplate redisTemplate = super.getRedisTemplate();
        return redisTemplate.opsForValue().get(MarketType.BINANCE.getMarketName());
    }

    /**
     * 새로운 공지사항 찾기
     */
    @Override
    public List<NoticeParsedData> getNewNotice(List<NoticeParsedData> recentNoticeDataList) {
        List<NoticeParsedData> previousNoticeParsedDataList = this.getNoticeData();
        List<NoticeParsedData> newNoticeParsedData = new ArrayList<>();

        Set<NoticeParsedData> parsedHashSet = new HashSet<>(previousNoticeParsedDataList);
        List<NoticeParsedData> recentNoticeCopy = new ArrayList<>(recentNoticeDataList);

        recentNoticeCopy.removeAll(parsedHashSet);
        newNoticeParsedData = recentNoticeCopy;

        if(newNoticeParsedData.isEmpty()){
            throw new IllegalStateException("not found new notice data : binance");
        }

        return newNoticeParsedData;
    }

    @Override
    public void setNewNotice(List<NoticeParsedData> notice){
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
        headers.setContentType(new MediaType("application", "json"));
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public List<NoticeParsedData> getNoticeData() {
        return this.parsedData;
    }

    @Override
    public String getNoticeUrl(){
        return this.noticeUrl;
    }

    @Override
    public boolean isNecessityOfDetour() {
        return true;
    }

    /**
     * Python 서비스를 통해 공지사항 데이터를 파싱
     */
    @Override
    public List<NoticeParsedData> parseNoticeData() throws IOException {
        try {
            log.info("Binance Python 서비스를 통한 공지사항 파싱 시작");
            
            // flask 서비스에서 공지사항 가져오기
            PythonNoticeResponseDto pythonResponse = super.fetchPythonNotices();
            
            // flask 응답을 NoticeParsedData로 변환
            List<NoticeParsedData> noticeParsedDataList = super.convertPythonToNoticeParsedData(pythonResponse);
            
            if (noticeParsedDataList.isEmpty()) {
                log.warn("Binance Python 서비스에서 공지사항을 가져오지 못했습니다");
                return new ArrayList<>();
            }
            
            log.info("Binance 공지사항 {} 개 파싱 완료", noticeParsedDataList.size());
            return noticeParsedDataList;
            
        } catch (Exception e) {
            log.error("Binance 공지사항 파싱 실패: {}", e.getMessage(), e);
            throw new IOException("Binance 공지사항 파싱 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public String getAbsoluteUrl() {
        return this.noticeDetailAbsoluteUrl;
    }

    @Override
    public MarketType getMarketType() {
        return MarketType.BINANCE;
    }
}
