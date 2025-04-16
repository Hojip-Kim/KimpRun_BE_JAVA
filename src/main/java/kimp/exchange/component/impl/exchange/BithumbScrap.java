package kimp.exchange.component.impl.exchange;

import kimp.exchange.dto.bithumb.BithumbNoticeDto;
import kimp.exchange.dto.notice.NoticeParsedData;
import kimp.market.Enum.MarketType;
import kimp.util.MumurHashUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
@Slf4j
public class BithumbScrap extends ExchangeScrapAbstract<BithumbNoticeDto> {


    @Value("${client.url.request}")
    private String clientRequestUrl;

    @Value("${bithumb.notice.url}")
    private String noticeUrl;

    @Value("${bithumb.notice.detail.url}")
    private String bithumbNoticeDetailUrl;

    private List<NoticeParsedData> parsedData = new ArrayList<>();

    private List<NoticeParsedData> newNotices = new ArrayList<>();

    public BithumbScrap(RestTemplate restTemplate, StringRedisTemplate stringRedisTemplate, @Qualifier("redisTemplate") RedisTemplate redisTemplate) {
        super(restTemplate, stringRedisTemplate, BithumbNoticeDto.class);
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
        super.getRedisTemplate().opsForValue().set(MarketType.BITHUMB.getMarketName(), noticeHashCode);

    }

    @Override
    public String getNoticeFromRedis() {
        StringRedisTemplate redisTemplate = super.getRedisTemplate();
        return redisTemplate.opsForValue().get(MarketType.BITHUMB.getMarketName());
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
        return HttpMethod.POST;
    }

    @Override
    public List<NoticeParsedData> getNoticeData(){

        return this.parsedData;
    }

    @Override
    public String getNoticeUrl() {
        return this.noticeUrl;
    }

    @Override
    public boolean isNecessityOfDetour() {
        return true;
    }

    @Override
    public List<NoticeParsedData> parseNoticeData() throws IOException {

        List<NoticeParsedData> noticeParsedDataList = new ArrayList<>();

        BithumbNoticeDto notice = super.getNoticeFromAPI();
        String htmlSource = notice.getData();

        Document document = Jsoup.parse(htmlSource);

        Elements noticeTitles = document.select(".NoticeContentList_notice-list__link-title__nlmSC");
        Elements aLinks = document.select(".NoticeContentList_notice-list__i337r a");
        Elements noticeDates = document.select(".NoticeContentList_notice-list__link-date__gDc6U");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

        for (int i = 0; i < noticeTitles.size(); i++) {
            String titleData = noticeTitles.get(i).text();
            String linkData = aLinks.get(i).attr("href");
            String dateData = noticeDates.get(i).text()+ " 00:00:00";

            LocalDateTime parsedLocalDate = LocalDateTime.parse(dateData, formatter);

            NoticeParsedData parseData = new NoticeParsedData(titleData, linkData, parsedLocalDate);

            noticeParsedDataList.add(parseData);

        }

        if(noticeParsedDataList.size() != noticeTitles.size()){
            log.info("bithumbScrap parsedDataList is inaccurate.");
            throw new IllegalStateException("bithumbScrap parsedDataList is inaccurate.");
        }

        return noticeParsedDataList;
    }

    // 빗썸은 메인 공지사항 api를 사용했을 때 localdatetime의 시간, 분이 안나오므로 시간, 분 데이터를 위해 공지 내부에 접근하여 시간,분 데이터를 뽑아내야 함.
    public List<NoticeParsedData> parseNoticeDetailData(String detailUrl) throws IOException {
        String localGateway = this.clientRequestUrl;

        String webUrl = this.getAbsoluteUrl() + detailUrl;

        Map<String, String> body = new HashMap<>();
        body.put("url", webUrl);

        System.out.println("webUrl : " + webUrl);

        HttpEntity<?> httpEntity = new HttpEntity<>(body, getHeaders());

        ResponseEntity<BithumbNoticeDto> res = super.getRestTemplate().exchange(localGateway, this.getMethod(), httpEntity, getResponseType());

        String detailHtmlSource = res.getBody().getData();

        System.out.println(detailHtmlSource);


        return null;
    }

    @Override
    public String getAbsoluteUrl() {
        return this.bithumbNoticeDetailUrl;
    }

    @Override
    public MarketType getMarketType() {
        return MarketType.BITHUMB;
    }

}
