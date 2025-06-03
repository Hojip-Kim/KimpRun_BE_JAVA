package kimp.config.application;

import kimp.market.handler.BinanceWebsocketHandler;
import kimp.market.handler.MarketInfoHandler;
import kimp.market.handler.UpbitWebSocketHandler;
import kimp.chat.handler.ChatWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@Slf4j
public class WebsocketConfig implements WebSocketConfigurer {

    @Value("${environment.websocket.allowOrigins}")
    private String allowOrigins;

    private final UpbitWebSocketHandler upbitWebSocketHandler;
    private final BinanceWebsocketHandler binanceWebsocketHandler;

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final MarketInfoHandler marketInfoHandler;

    public WebsocketConfig(UpbitWebSocketHandler upbitWebSocketHandler, BinanceWebsocketHandler binanceWebsocketHandler, ChatWebSocketHandler chatWebSocketHandler, MarketInfoHandler marketInfoHandler) {
        this.upbitWebSocketHandler = upbitWebSocketHandler;
        this.binanceWebsocketHandler = binanceWebsocketHandler;
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.marketInfoHandler = marketInfoHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("allowOrigins : " + allowOrigins);
        registry
                .addHandler(upbitWebSocketHandler, "/upbit")
                .setAllowedOrigins(allowOrigins);
        registry
                .addHandler(binanceWebsocketHandler, "/binance")
                .setAllowedOrigins(allowOrigins);
        registry
                .addHandler(chatWebSocketHandler, "/chatService")
                .setAllowedOrigins(allowOrigins);
        registry
                .addHandler(marketInfoHandler, "/marketInfo")
                .setAllowedOrigins(allowOrigins);

    }
}
