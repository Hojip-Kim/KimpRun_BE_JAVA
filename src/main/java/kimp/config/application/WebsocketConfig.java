package kimp.config.application;

import kimp.market.handler.*;
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
    private final CoinoneWebsocketHandler coinoneWebsocketHandler;
    private final BithumbWebsocketHandler bithumbWebsocketHandler;

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final MarketInfoHandler marketInfoHandler;

    public WebsocketConfig(UpbitWebSocketHandler upbitWebSocketHandler, BinanceWebsocketHandler binanceWebsocketHandler, CoinoneWebsocketHandler coinoneWebsocketHandler, BithumbWebsocketHandler bithumbWebsocketHandler, ChatWebSocketHandler chatWebSocketHandler, MarketInfoHandler marketInfoHandler) {
        this.upbitWebSocketHandler = upbitWebSocketHandler;
        this.binanceWebsocketHandler = binanceWebsocketHandler;
        this.coinoneWebsocketHandler = coinoneWebsocketHandler;
        this.bithumbWebsocketHandler = bithumbWebsocketHandler;
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
                .addHandler(bithumbWebsocketHandler, "/bithumb")
                .setAllowedOrigins(allowOrigins);
        registry
                .addHandler(coinoneWebsocketHandler, "/coinone")
                .setAllowedOrigins(allowOrigins);
        registry
                .addHandler(chatWebSocketHandler, "/chatService")
                .setAllowedOrigins(allowOrigins);
        registry
                .addHandler(marketInfoHandler, "/marketInfo")
                .setAllowedOrigins(allowOrigins);

    }
}
