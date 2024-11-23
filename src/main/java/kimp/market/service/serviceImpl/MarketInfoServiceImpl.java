package kimp.market.service.serviceImpl;

import kimp.market.components.Dollar;
import kimp.market.service.MarketInfoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MarketInfoServiceImpl implements MarketInfoService {

    private final Dollar dollar;

    private double dollarKRW = 0;

    public MarketInfoServiceImpl(Dollar dollar) {
        this.dollar = dollar;
    }

    @Override
    public double getDollarKRW(){

        return this.dollarKRW;
    }

    // scheduling 3분마다 호출 (무료 plan은 하루 호출제한 1500회이므로 3분으로 책정)
    @Scheduled(fixedRate = 3*60*1000)
    private void dollarSet() throws IOException {
        if(dollar != null) {
            this.dollarKRW = (double) Math.round(dollar.getUSDKRW() * 10) /10;
        }
    }
}
