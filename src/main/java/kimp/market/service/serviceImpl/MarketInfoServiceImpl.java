package kimp.market.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import kimp.market.components.Dollar;
import kimp.market.components.Upbit;
import kimp.market.service.MarketInfoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class MarketInfoServiceImpl implements MarketInfoService {

    private final Dollar dollar;
    private final Upbit upbit;

    private double dollarKRW = 0;
    private double usdt = 0;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MarketInfoServiceImpl(Dollar dollar, RestTemplate restTemplate, ObjectMapper objectMapper, Upbit upbit) {
        this.dollar = dollar;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.upbit = upbit;
    }

    @PostConstruct
    public void init() throws IOException {
        if(dollar != null) {
            this.dollarKRW = (double) Math.round(dollar.getUSDKRW() * 10) /10;
        }
        if(upbit != null) {
            this.usdt = upbit.getUpbitTether().doubleValue();

        }
    }

    @Override
    public double getDollarKRW(){

        return this.dollarKRW;
    }

    @Override
    public double getTetherKRW() {

        return this.usdt;
    }


    // scheduling 3분마다 호출 (무료 plan은 하루 호출제한 1500회이므로 3분으로 책정)
    @Scheduled(fixedRate = 3*60*1000)
    private void infoSet() throws IOException {
        if(dollar != null) {
            this.dollarKRW = (double) Math.round(dollar.getUSDKRW() * 10) /10;
        }
        if(upbit != null) {
            this.usdt = upbit.getUpbitTether().doubleValue();

        }
    }
}
