package kimp.scrap.service.impl;

import kimp.scrap.component.ExchangeScarp;
import kimp.scrap.component.impl.exchange.ExchangeScrapAbstract;
import kimp.scrap.service.ScrapService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ScrapServiceImpl implements ScrapService {

    private final ExchangeScarp exchangeScarp;

    public ScrapServiceImpl(@Qualifier("upbitScrap") ExchangeScrapAbstract exchangeScarp) {
        this.exchangeScarp = exchangeScarp;
    }

//    public String restTemplateTest

}
