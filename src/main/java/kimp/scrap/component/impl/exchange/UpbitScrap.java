package kimp.scrap.component.impl.exchange;

import kimp.scrap.dto.upbit.UpbitNoticeDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Qualifier("upbitScrap")
public class UpbitScrap extends ExchangeScrapAbstract<UpbitNoticeDto> {

    public UpbitScrap(RestTemplate restTemplate, StringRedisTemplate stringRedisTemplate) {
        super(restTemplate, stringRedisTemplate);
    }

    @Override
    protected Class<UpbitNoticeDto> getResponseType() {
        return UpbitNoticeDto.class;
    }

    @Override
    protected void setNoticeToRedis(){

    }

    @Override
    protected void getNoticeFromRedis() {

    }

    @Override
    protected String getNewNotice() {
        return "";
    }

    @Override
    protected UpbitNoticeDto getNoticeData() {
        return null;
    }

}
