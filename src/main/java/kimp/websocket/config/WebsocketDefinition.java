package kimp.websocket.config;

import kimp.market.components.Upbit;
import kimp.market.service.serviceImpl.MarketServiceImpl;
import kimp.websocket.client.BinanceWebSocketClient;
import kimp.websocket.handler.WebSocketHandler;
import kimp.websocket.client.UpbitWebsocketClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@RequiredArgsConstructor
public class WebsocketDefinition {

    @Value("${upbit.websocket.url}")
    private String upbitWebsocketUrl;

    @Value("${binance.websocket.url}")
    private String binanceWebsocketUrl;

    private final Upbit upbit;
    /*
     * @TODO
     *   streamNames and wsUrl 환경변수설정
     * */
    @Bean
    public BinanceWebSocketClient binanceWebSocketClient(MarketServiceImpl marketServiceImpl) throws URISyntaxException, IOException {


        String streamNames = String.join("/",
                "btcusdt@trade",
                "ethusdt@trade",
                "xrpusdt@trade",
                "iqusdt@trade",
                "idusdt@trade",
                "scusdt@trade",
                "zrxusdt@trade",
                "pythusdt@trade"
        );

        String wsUrl = String.format(binanceWebsocketUrl, streamNames);
        URI uri = new URI(wsUrl);

        BinanceWebSocketClient client = new BinanceWebSocketClient(uri);
        client.connect();

        return client;
    }


    @Bean
    public UpbitWebsocketClient upbitWebsocketClient(WebSocketHandler webSocketHandler) throws URISyntaxException, InterruptedException {

        UpbitWebsocketClient client = new UpbitWebsocketClient(upbitWebsocketUrl, webSocketHandler, upbit);

        client.connectBlocking();

        return client;
    }

}
