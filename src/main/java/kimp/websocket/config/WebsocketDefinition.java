package kimp.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.components.impl.market.Binance;
import kimp.market.components.impl.market.Bithumb;
import kimp.market.components.impl.market.Upbit;
import kimp.market.controller.MarketDataStompController;
import kimp.market.service.MarketInfoService;
import kimp.market.service.MarketService;
import kimp.websocket.client.BinanceWebSocketClient;
import kimp.websocket.client.BithumbWebsocketClient;
import kimp.websocket.client.UpbitWebsocketClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class WebsocketDefinition {

    @Value("${upbit.websocket.url}")
    private String upbitWebsocketUrl;

    @Value("${binance.websocket.url}")
    private String binanceWebsocketUrl;

    @Value("${bithumb.websocket.url}")
    private String bithumbWebsocketUrl;

    private final Upbit upbit;
    private final Binance binance;
    private final Bithumb bithumb;
    private final MarketInfoService marketInfoService;
    private final MarketDataStompController marketDataStompController;
    private final ObjectMapper objectMapper;

    private final MarketService marketService;

    public WebsocketDefinition(Upbit upbit, Binance binance, Bithumb bithumb, MarketService marketService, MarketInfoService marketInfoService, MarketDataStompController marketDataStompController, ObjectMapper objectMapper) {
        this.upbit = upbit;
        this.binance = binance;
        this.bithumb = bithumb;
        this.marketService = marketService;
        this.marketInfoService = marketInfoService;
        this.marketDataStompController = marketDataStompController;
        this.objectMapper = objectMapper;
    }

    @Bean
    public BinanceWebSocketClient binanceWebSocketClient() throws URISyntaxException, InterruptedException, IOException {

        List<String> binanceMarketPair = binance.getMarketList().getPairList();

        String streamNames = binanceMarketPair.stream()
                .map(data -> data.toLowerCase() + "usdt@trade")
                .collect(Collectors.joining("/"));

        String wsUrl = String.format(binanceWebsocketUrl, streamNames);

        BinanceWebSocketClient client = new BinanceWebSocketClient(wsUrl, objectMapper,marketDataStompController, marketInfoService, binance);
        client.connectBlocking();

        return client;
    }


    @Bean
    public UpbitWebsocketClient upbitWebsocketClient() throws URISyntaxException, InterruptedException {

        UpbitWebsocketClient client = new UpbitWebsocketClient(upbitWebsocketUrl, objectMapper, marketDataStompController, upbit);

        client.connectBlocking();

        return client;
    }

    @Bean
    public BithumbWebsocketClient bithumbWebsocketClient() throws URISyntaxException, InterruptedException {
        BithumbWebsocketClient client = new BithumbWebsocketClient(bithumbWebsocketUrl, objectMapper, marketDataStompController, bithumb);

        client.connectBlocking();

        return client;
    }
}
