package kimp.exchange.component.impl.exchange;

import kimp.exchange.dto.notice.NoticeParsedData;
import kimp.exchange.dto.upbit.UpbitNoticeDataDto;
import kimp.exchange.dto.upbit.UpbitNoticeDto;
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
public class UpbitScrap extends ExchangeScrapAbstract<UpbitNoticeDto> {

    @Value("${upbit.notice.url}")
    private String noticeUrl;

    @Value("${upbit.notice.detail.url}")
    private String upbitNoticeDetailUrl;

    private List<NoticeParsedData> parsedData = new ArrayList<>();

    private List<NoticeParsedData> newNotices = new ArrayList<>();

    public UpbitScrap(RestTemplate restTemplate, StringRedisTemplate stringRedisTemplate) {
        super(restTemplate, stringRedisTemplate, UpbitNoticeDto.class);
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
        super.getRedisTemplate().opsForValue().set(MarketType.UPBIT.getMarketName(), noticeHashCode);

    }

    @Override
    public String getNoticeFromRedis() {
        StringRedisTemplate redisTemplate = super.getRedisTemplate();

        return redisTemplate.opsForValue().get(MarketType.UPBIT.getMarketName());
    }

    // 새로운 데이터를 두 리스트의 비교 (Big(O^2)성능)의 형태로 구현
    // 이후 리팩토링을 통해 성능개선 필요
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

    // 레디스에 올리는것이 아닌, 새로운 notice만 제공
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

    @Override
    public List<NoticeParsedData> parseNoticeData() throws IOException {

        List<NoticeParsedData> noticeParsedDataList = new ArrayList<>();

        UpbitNoticeDto dto = super.getNoticeFromAPI();
        List<UpbitNoticeDataDto> upbitNoticeDataDtos = dto.getData().getNotices();

        for(UpbitNoticeDataDto upbitNoticeDataDto : upbitNoticeDataDtos) {
            String title = upbitNoticeDataDto.getTitle();
            String alink = String.valueOf(upbitNoticeDataDto.getId());
            LocalDateTime date = upbitNoticeDataDto.getListed_at().toLocalDateTime();

            NoticeParsedData noticeParsedData = new NoticeParsedData(title, alink, date);
            noticeParsedDataList.add(noticeParsedData);
        }

        if(noticeParsedDataList.size() != upbitNoticeDataDtos.size()) {
            log.info("upbitScrap parsedDataList is inaccurate");
            throw new IllegalStateException("upbitScrap parsedDataList is inaccurate");
        }

        return noticeParsedDataList;
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
