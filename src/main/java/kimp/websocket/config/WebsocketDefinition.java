package kimp.websocket.config;

import kimp.market.components.Binance;
import kimp.market.components.Upbit;
import kimp.market.service.serviceImpl.MarketServiceImpl;
import kimp.websocket.client.BinanceWebSocketClient;
import kimp.websocket.handler.BinanceWebsocketHandler;
import kimp.websocket.handler.UpbitWebSocketHandler;
import kimp.websocket.client.UpbitWebsocketClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class WebsocketDefinition {

    @Value("${upbit.websocket.url}")
    private String upbitWebsocketUrl;

    @Value("${binance.websocket.url}")
    private String binanceWebsocketUrl;

    private final Upbit upbit;
    private final Binance binance;

    @Bean
    public BinanceWebSocketClient binanceWebSocketClient(BinanceWebsocketHandler binanceWebsocketHandler, MarketServiceImpl marketServiceImpl) throws URISyntaxException, InterruptedException, IOException {
    List<String> binanceMarketPair = marketServiceImpl.getMarketPair(upbit, binance);

        binanceMarketPair = binanceMarketPair.stream().map(data -> data.toLowerCase()).map(data -> data + "usdt@trade").collect(Collectors.toList());;

        String marketPairToString = binanceMarketPair.toString().replace("[", "").replace("]", "").replace(", ", "/");

        String streamNames = String.join("/", marketPairToString);

        String wsUrl = String.format(binanceWebsocketUrl, streamNames);

        BinanceWebSocketClient client = new BinanceWebSocketClient(wsUrl, binanceWebsocketHandler);
        client.connectBlocking();

        return client;
    }


    @Bean
    public UpbitWebsocketClient upbitWebsocketClient(UpbitWebSocketHandler upbitWebSocketHandler) throws URISyntaxException, InterruptedException {

        UpbitWebsocketClient client = new UpbitWebsocketClient(upbitWebsocketUrl, upbitWebSocketHandler, upbit);

        client.connectBlocking();

        return client;
    }

}
