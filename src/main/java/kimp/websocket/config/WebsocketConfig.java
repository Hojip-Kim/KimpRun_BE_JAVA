package kimp.websocket.config;

import kimp.websocket.handler.BinanceWebsocketHandler;
import kimp.websocket.handler.UpbitWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    private final UpbitWebSocketHandler upbitWebSocketHandler;
    private final BinanceWebsocketHandler binanceWebsocketHandler;

    public WebsocketConfig(UpbitWebSocketHandler upbitWebSocketHandler, BinanceWebsocketHandler binanceWebsocketHandler) {
        this.upbitWebSocketHandler = upbitWebSocketHandler;
        this.binanceWebsocketHandler = binanceWebsocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(upbitWebSocketHandler, "/upbit")
                .setAllowedOrigins("*");
        registry
                .addHandler(binanceWebsocketHandler, "/binance")
                .setAllowedOrigins("*");


    }
}
