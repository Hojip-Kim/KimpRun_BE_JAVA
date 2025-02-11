package kimp.scrap.component.impl.exchange;

import kimp.exception.KimprunException;
import kimp.exception.KimprunExceptionEnum;
import kimp.scrap.component.ExchangeScarp;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;


public abstract class ExchangeScrapAbstract<T> implements ExchangeScarp<T> {

    private final RestTemplate restTemplate;
    private final StringRedisTemplate redisTemplate;

    public ExchangeScrapAbstract(RestTemplate restTemplate, StringRedisTemplate redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    public T getNoticeFromAPI(URI webSiteUrl) throws IOException {
        try {
            T response = restTemplate.getForObject(webSiteUrl, getResponseType());

            if (response == null) {
                throw new IOException();
            }

            return response;

        }catch(Exception E){
            throw new KimprunException(KimprunExceptionEnum.INTERNAL_SERVER_ERROR, "getNoticeAPI Error", HttpStatus.INTERNAL_SERVER_ERROR, "trace");
        }
    }


    protected abstract Class<T> getResponseType();

    protected abstract void setNoticeToRedis();

    protected abstract void getNoticeFromRedis();

    protected abstract String getNewNotice();

    protected abstract T getNoticeData();







}
