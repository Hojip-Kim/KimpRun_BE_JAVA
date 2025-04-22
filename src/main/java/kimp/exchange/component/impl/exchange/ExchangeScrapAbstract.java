package kimp.exchange.component.impl.exchange;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.exchange.component.ExchangeScrap;
import kimp.exchange.dto.notice.NoticeParsedData;
import kimp.exchange.dto.notice.NoticeResponseDto;
import kimp.market.Enum.MarketType;
import kimp.util.MumurHashUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Slf4j
public abstract class ExchangeScrapAbstract<T> implements ExchangeScrap<T> {

    private final RestTemplate restTemplate;
    private final StringRedisTemplate redisTemplate;
    private final Class<T> responseType;

    @Value("${admin.gateway}")
    private String adminGateway;

    public ExchangeScrapAbstract(RestTemplate restTemplate, StringRedisTemplate redisTemplate, Class<T> responseType) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
        this.responseType = responseType;
    }

    @Override
    public T getNoticeFromAPI() throws IOException {
            String apiUrl = this.getNoticeUrl();

            HttpHeaders headers = this.getHeaders();

            HttpEntity<?> httpEntity = null;
            String websiteUrl;

            if(this.getMethod().equals(HttpMethod.GET)) {

                httpEntity = new HttpEntity<>(headers);

            }else if(this.getMethod().equals(HttpMethod.POST)) {

                Map<String, String> body = new HashMap<>();

                if(this.isNecessityOfDetour()) {
                    websiteUrl = apiUrl.toString();
                    apiUrl = adminGateway;
                    body.put("url", websiteUrl);
                }

                httpEntity = new HttpEntity<>(body, headers);

            }

            ResponseEntity<T> response = restTemplate.exchange(
                    apiUrl, this.getMethod(), httpEntity, getResponseType()
            );

            if(response == null || response.getBody() == null) {
                throw new KimprunException(KimprunExceptionEnum.INTERNAL_SERVER_ERROR, "getNoticeAPI Error - " + getResponseType().toString(), HttpStatus.INTERNAL_SERVER_ERROR, "trace");
            }

            return response.getBody();
    }

    protected StringRedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }

    protected RestTemplate getRestTemplate() {
        return this.restTemplate;
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
