package kimp.scrap.component.impl.exchange;

import kimp.scrap.dto.coinone.CoinoneNoticeDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Qualifier("coinoneScrap")
public class CoinoneScrap extends ExchangeScrapAbstract<CoinoneNoticeDto> {

    public CoinoneScrap(RestTemplate restTemplate, StringRedisTemplate stringRedisTemplate) {
        super(restTemplate, stringRedisTemplate);
    }

    @Override
    protected Class<CoinoneNoticeDto> getResponseType() {
        return CoinoneNoticeDto.class;
    }

    @Override
    protected void setNoticeToRedis(){

    }

    @Override
    protected void getNoticeFromRedis() {

    }

    @Override
    protected String getNewNotice(){
        return "";
    }

    @Override
    protected CoinoneNoticeDto getNoticeData() {
        return null;
    }

}
