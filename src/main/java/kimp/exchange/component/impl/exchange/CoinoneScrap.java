package kimp.exchange.component.impl.exchange;

import kimp.exchange.dto.coinone.CoinoneNoticeDto;
import kimp.exchange.dto.coinone.CoinoneNoticeResultDto;
import kimp.exchange.dto.notice.NoticeParsedData;
import kimp.market.Enum.MarketType;
import kimp.util.MumurHashUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
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

    public CoinoneScrap(RestTemplate restTemplate, StringRedisTemplate stringRedisTemplate) {
        super(restTemplate, stringRedisTemplate, CoinoneNoticeDto.class);
    }


    @Override
    public List<NoticeParsedData> getFieldNewNotice() {
        return this.newNotices;
    }

    @Override
    public void setNoticeToRedis(List<NoticeParsedData> noticeParsedDataList){

        StringBuilder sb = new StringBuilder();

        for(NoticeParsedData noticeParsedData : noticeParsedDataList){
            sb.append(noticeParsedData.getTitle());
        }

        String noticeHashCode = MumurHashUtil.stringTo128bitHashCode(sb.toString());
        super.getRedisTemplate().opsForValue().set(MarketType.COINONE.getMarketName(), noticeHashCode);


    }

    @Override
    public String getNoticeFromRedis() {
        return super.getRedisTemplate().opsForValue().get(MarketType.COINONE.getMarketName());
    }

    @Override
    public List<NoticeParsedData> getNewNotice(List<NoticeParsedData> recentNoticeDataList){
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

    @Override
    public List<NoticeParsedData> parseNoticeData() throws IOException {

        List<NoticeParsedData> noticeParsedDataList = new ArrayList<>();

        CoinoneNoticeDto dto = super.getNoticeFromAPI();

        List<CoinoneNoticeResultDto> coinoneNoticeResultDto = dto.getResults();

        for(CoinoneNoticeResultDto result : coinoneNoticeResultDto){
            String title = result.getTitle();
            String alink = result.getGet_absolute_url();
            LocalDateTime date = result.getCreatedAt().toLocalDateTime();

            NoticeParsedData noticeParsedData = new NoticeParsedData(title, alink, date);
            noticeParsedDataList.add(noticeParsedData);
        }

        if(noticeParsedDataList.size() != coinoneNoticeResultDto.size()){
            log.info("coinoneScrap parsedDataList is inaccurate");
            throw new IllegalStateException("coinoneScrap parsedDataList is inaccurate");
        }

        return noticeParsedDataList;
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
