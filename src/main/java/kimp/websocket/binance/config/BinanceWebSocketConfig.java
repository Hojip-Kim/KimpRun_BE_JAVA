package kimp.websocket.binance.config;

import kimp.websocket.binance.BinanceWebSocketClient;
import org.springframework.context.annotation.Bean;

import java.net.URI;
import java.net.URISyntaxException;

//@Configuration
public class BinanceWebSocketConfig {


   /*
   * @TODO
   *   streamNames and wsUrl 환경변수설정
   * */

    @Bean
    public BinanceWebSocketClient binanceWebSocketClient() throws URISyntaxException {
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

        String wsUrl = String.format("wss://stream.binance.com:9443/stream?streams=%s", streamNames);
        URI uri = new URI(wsUrl);

        BinanceWebSocketClient client = new BinanceWebSocketClient(uri);
        client.connect();

        return client;
    }

}
