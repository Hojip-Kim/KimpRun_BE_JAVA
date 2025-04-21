package kimp.scrap.component.impl.exchange;

import kimp.scrap.dto.binance.BinanceNotice;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@Qualifier("binanceScrap")
public class BinanceScrap extends ExchangeScrapAbstract<BinanceNotice> {

    BinanceScrap(RestTemplate restTemplate, StringRedisTemplate stringRedisTemplate) {
        super(restTemplate, stringRedisTemplate);
    }

    @Override
    protected Class<BinanceNotice> getResponseType() {
        return BinanceNotice.class;
    }

    @Override
    protected void setNoticeToRedis() {

    }

    @Override
    protected void getNoticeFromRedis() {

    }

    @Override
    protected String getNewNotice() {
        return "";
    }

    @Override
    protected BinanceNotice getNoticeData() {
        return null;
    }


}
