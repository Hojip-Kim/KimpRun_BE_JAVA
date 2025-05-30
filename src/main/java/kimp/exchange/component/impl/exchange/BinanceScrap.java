package kimp.exchange.component.impl.exchange;

import kimp.exchange.dto.binance.BinanceArticleDto;
import kimp.exchange.dto.binance.BinanceNoticeDto;
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
import org.springframework.web.client.UnknownContentTypeException;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    BinanceScrap(RestTemplate restTemplate, StringRedisTemplate stringRedisTemplate) {
        super(restTemplate, stringRedisTemplate, BinanceNoticeDto.class);
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

    @Override
    public List<NoticeParsedData> parseNoticeData() throws IOException {
            List<NoticeParsedData> noticeParsedDataList = new ArrayList<>();


        try {
            BinanceNoticeDto binanceNoticeDto = super.getNoticeFromAPI();

            List<BinanceArticleDto> binanceArticleDtos = binanceNoticeDto.getData().getData().getCatalogs().get(0).getArticles();

            for (BinanceArticleDto binanceArticleDto : binanceArticleDtos) {
                String title = binanceArticleDto.getTitle();
                String alink = binanceArticleDto.getCode();

                Long date = binanceArticleDto.getReleaseDate();
                Instant instant = Instant.ofEpochMilli(date);
                LocalDateTime localDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

                NoticeParsedData noticeParsedData = new NoticeParsedData(title, alink, localDate);
                noticeParsedDataList.add(noticeParsedData);
            }

            if (noticeParsedDataList.size() != binanceArticleDtos.size()) {
                log.info("binanceScrap parsedDataList is inaccurate.");
                throw new IllegalStateException("binanceScrap parsedDataList is inaccurate.");
            }

            return noticeParsedDataList;
        }catch(UnknownContentTypeException e) {

            log.error("error occurred when parsing binance notice data : ", e);
            return null;
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
